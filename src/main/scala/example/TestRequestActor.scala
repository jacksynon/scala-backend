package example

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class TestRequestActor extends Actor with JsonSupport {
  implicit val system = context.system
  private val http = Http(system)

  // Cache variables
  private var cachedPosts: Option[List[Post]] = None
  private var cachedComments: Option[List[Comment]] = None
  private var cacheTimestamp: Long = 0L
  private val cacheTTL = 5.minutes.toMillis  // Set TTL to 5 minutes

  def receive: Receive = {
    case "handleRequest" =>
      val currentTime = System.currentTimeMillis()
      val shouldFetch = currentTime - cacheTimestamp > cacheTTL

      // Determine whether to fetch new data or use cache, with logging
      val postsFuture = if (shouldFetch || cachedPosts.isEmpty) {
        println("Fetching new posts data...")
        fetchPosts()
      } else {
        println("Using cached posts data.")
        Future.successful(cachedPosts.get)
      }

      val commentsFuture = if (shouldFetch || cachedComments.isEmpty) {
        println("Fetching new comments data...")
        fetchComments()
      } else {
        println("Using cached comments data.")
        Future.successful(cachedComments.get)
      }

      // Update cache after fetching new data
      postsFuture.foreach { posts =>
        cachedPosts = Some(posts)
        cacheTimestamp = currentTime
      }
      commentsFuture.foreach { comments =>
        cachedComments = Some(comments)
      }

      // Combine posts and comments into the desired format
      val resultFuture = for {
        posts <- postsFuture
        comments <- commentsFuture
      } yield {
        val firstFivePosts = posts.take(5)
        val groupedComments = comments.groupBy(_.postId).mapValues(_.take(2))
        firstFivePosts.map(post => PostWithComments(post, groupedComments.getOrElse(post.id, List())))
      }

      resultFuture pipeTo sender()
  }

  private def fetchPosts(): Future[List[Post]] = {
    http.singleRequest(HttpRequest(uri = "https://jsonplaceholder.typicode.com/posts"))
      .flatMap(response => Unmarshal(response.entity).to[List[Post]])
  }

  private def fetchComments(): Future[List[Comment]] = {
    http.singleRequest(HttpRequest(uri = "https://jsonplaceholder.typicode.com/comments"))
      .flatMap(response => Unmarshal(response.entity).to[List[Comment]])
  }
}

object TestRequestActor {
  def props: Props = Props[TestRequestActor]
}