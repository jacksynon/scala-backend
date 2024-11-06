package example

// Case classes to model the data
case class Post(userId: Int, id: Int, title: String, body: String)
case class Comment(postId: Int, id: Int, name: String, email: String, body: String)
case class PostWithComments(post: Post, comments: List[Comment])

// JSON formats for Akka HTTP JSON marshalling/unmarshalling
trait JsonSupport extends spray.json.DefaultJsonProtocol {
  implicit val postFormat = jsonFormat4(Post)
  implicit val commentFormat = jsonFormat5(Comment)
  implicit val postWithCommentsFormat = jsonFormat2(PostWithComments)
}
