# Scala Akka HTTP Example

This project is a simple backend application built with Scala and Akka HTTP. It demonstrates how to set up an HTTP server with caching, actor-based request handling, and JSON responses.

## Project Structure

- **`Main.scala`**: Sets up the Akka HTTP server and defines routes for `/health` and `/test`.
- **`TestRequestActor.scala`**: An Akka actor responsible for handling requests to the `/test` route. This actor fetches data from external APIs, caches the results, and combines responses for efficient processing.
- **`Models.scala`**: Defines case classes to model the data structure (`Post`, `Comment`, `PostWithComments`) and provides JSON support for marshalling/unmarshalling using Spray JSON.

## Features

- **Actor-Based Request Handling**: Requests to `/test` are handled by `TestRequestActor`, which processes each request asynchronously.
- **Caching with TTL**: The actor caches the fetched data (posts and comments) for 5 minutes to reduce redundant API calls and improve performance.
- **External API Integration**: Fetches data from `https://jsonplaceholder.typicode.com`:
  - `GET /posts`: Fetches posts.
  - `GET /comments`: Fetches comments.
- **JSON Responses**: Combines the first 5 posts with the first 2 comments for each, formatted as JSON.

## Endpoints

### Health Check

- **`GET /health`**
- Response: `"Service is running"`

### Test Data Endpoint

- **`GET /test`**
- Description: Returns the first 5 posts from the API, each with the first 2 associated comments.
- Response Example:
  ```json
  [
    {
      "post": {
        "userId": 1,
        "id": 1,
        "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
        "body": "quia et suscipit..."
      },
      "comments": [
        {
          "postId": 1,
          "id": 1,
          "name": "id labore ex et quam laborum",
          "email": "Eliseo@gardner.biz",
          "body": "laudantium enim quasi est quidem magnam..."
        },
        {
          "postId": 1,
          "id": 2,
          "name": "quo vero reiciendis velit similique earum",
          "email": "Jayne_Kuhic@sydney.com",
          "body": "est natus enim nihil est dolore omnis..."
        }
      ]
    },
    ...
  ]
  ```
