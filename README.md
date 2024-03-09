# Project Description:
This project demonstrates the OAuth 2.0 authorization code flow with Okta. OAuth 2.0 is an authorization protocol used in our demo application to enable users to access resources without sharing their passwords with the website they're accessing. Instead, users authenticate via trusted identity providers such as Google, Facebook, or Okta.

The entry point of this application is the client UI, where users can choose to log in either as a hardcoded user or using OAuth 2.0. If they select OAuth 2.0, they are redirected to the Okta Authorization Server, where they must provide their credentials, as illustrated in the diagram below (step 1). Upon successful authentication, Okta returns authorization code to client UI (step 2).
After that client UI sends authorization code to client-server-auth (step 3) which then exchanges it with Okta fro an ID token and an access token (step 4)

Finally, the access token is used to access resources exposed by the resource server, which is otherwise protected. This allows users to interact with the exposed API from the resource server.

# Project Diagram
![image](https://github.com/zstojkovic00/OAuth2.0/assets/57263190/a6054dce-a383-485f-823b-37818cedde96)

## API documentation
### **client-auth-server endpoints:**
@GetMapping("/oauth2/callback") 
- api-url: ``` localhost:8080/oauth2/callback?code={code} ```
- Requires @RequestParam String code: authorization code
- Returns user details information
    ``` json
  access_token: "eyJraWQiOiJZMHpua1pQTDJWVUZDMXlkcENYR1RIc1J1NlE4aDRJOUJFdXdxYnBldk1RIiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULmJadDNYTEl4MmRwWVB1eTlpby1kTEVlQms4Zl9WbHFOcUFRVjBfbVc0UzgiLCJpc3MiOiJodHRwczovLKRldi04MDU1NjI3Ny5va3RhLmNvbS9vYXV0aDIvZGVmYXVsdCIsImF1ZCI6ImFwaTovL2RlZmF1bHQiLCJpYXQiOjE3MTAwMjE4ODgsImV4cCI6MTcxMDAyNTQ4OCwiY2lkIjoiMG9hZmk3eTloOFRvSVg0eE01ZDciLCJ1aWQiOiIwMHVmb21tOWlmZWMyeVNMNTVkNyIsInNjcCI6WyJzaW5naWR1bnVtLnJlYWQiLCJwcm9maWxlIiwiZW1haWwiLCJvcGVuaWQiXSwiYXV0aF90aW1lIjoxNzEwMDIxODI5LCJzdWIiOiIwMHplbGprb3N0b2prb3ZpY0BnbWFpbC5jb20ifQ.TkVw48D5mMMmjfWD7hn-Tt4Nb_clJsqaCGGxtSLjRQwYG1xPWyoeLlL86Sds3-B-2dYUv72UCmrDUTmH0kWAOdCpZbv-LJAoCfKKF-heW5u9RxUuqqbU7UCZYYA243ekdH27MsdHxIS8FXJtkdqSCdzChBjSuaR7vf05mCoSUl8H0luK1NaToTk4aOG6gF3o8l69g1XtIkzRGG1Y5jZARdqnJCp_iyK8-ejmUM8xwETZ5I-wDSqS434tIgOhASR2Y_XW9rlyQY3g9z4-eXrCe4p8Yyd2UcnMHQz1MbQ6_tjC5kTp6decSLGV9tJhsIYh0m9YYhOTjxsaUUYG0rBuhQ"
  attributes: {sub: '00ufomm9ifec2ySL55d7', name: 'Zeljko Stojkovic', email: 'test@gmail.com', ver: 1, iss: 'https://dev-80556277.okta.com/oauth2/default', …}
  username: "Zeljko Stojkovic"
    ```
@GetMapping("/exams")
- api-url: ``` localhost:8080/exams ```
- Requires @RequestHeader("Authorization"): access token
- Returns list of passed exams
    ``` json
    [
     {
        "code": "RSD1CC",
        "name": "Cloud computing",
        "points": 98.0,
        "grade": 10,
        "date": [
            2023,
            1,
            23
        ],
        "professor": "Nebojša Bačanin Džakula"
      }
  ]
    ```

## Prerequisites
- [`Java 17+`](https://www.oracle.com/java/technologies/downloads/#java17)
- [`npm`](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)
- [`Okta account`](https://developer.okta.com/signup/)


## Running applications
- ## **client-ui**
  - In a terminal, navigate to 'client-ui' folder
    - Create .env file and put your okta client id
  
    ``` 
    REACT_APP_CLIENT_ID={REACT_APP_CLIENT_ID} 
    ```

  - If you are running `client-ui` for the first time, execute the [`npm`](https://www.npmjs.com/) command below
      ```
      npm install
      ```
  - To start `client-ui` run
    ```
    npm start
    ```
- ## **client-auth-server**  
  - In a terminal, navigate to 'client-auth-server' folder

      - Inside of application.yml put Okta client and secert id

      ``` 
      client.id: {clientId}
      client.secret.id: {clientSecretId}
      ```
  - To start `client-auth-server` run

      ```
      ./mvnw clean spring-boot:run
      ```

- ## **resource-server**
  - In a terminal, navigate to 'resource-server' folder

    - Inside of application.yml change port to 8081

    ``` 
    server:
         port: 8081 
    ```
  - To start `resource-server` run

    ```
    ./mvnw clean spring-boot:run
    ```
