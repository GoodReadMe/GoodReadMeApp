# GoodReadMe
The app automatically updates the version of your library in the ReadMe file.

## How it works
App receives events about new releases -> App forks your repo -> App creates a pull request with the changes to your ReadMe file.

## Integrate with your project
### Use our [github action](https://github.com/GoodReadMe/GoodReadMeAction) (Recommended)

### By GitHub WebHook (Recommended)
**For the self host usage add a query `client_secret:<your client secret>`**
Go to Repository Settings -> WebHooks -> Add webhook 
 - Payload URL: `http://goodreadme.androidstory.dev/checkMe/byReleaseWebHook`
 - Content type: `application/json`
 - Which events would you like to trigger this webhook?: Let me select individual events and check Release. 
 
### Manually
**For the self host usage add a header `X-CLIENT-SECRET:<your client secret>` or `client_secret:<your client secret>`** 
Call server manually
```http request
POST http://goodreadme.androidstory.dev/checkMe/byRepoDetails
Content-Type: application/json

{
  "owner": "<Owner name>",
  "repo": "<Repo name>"
}
```
or
```http request
POST http://goodreadme.androidstory.dev/checkMe/byRepoFullName
Content-Type: application/json

{
  "fullName": "<Owner name>/<Repo name>"
}
```

## Setup the app for self host usage.
**For the self host add `CLIENT_SECRET` to environment variables** 
### Easy run (DockerHub)
```shell script
docker pull vovochkastelmashchuk/good-readme:1.0
docker run -p 80:8080 --env GITHUB_TOKEN=<Github token> -d --rm vovochkastelmashchuk/good-readme:1.0
```

### From source code with docker
Change github.token in [application.conf](resources/application.conf)
1. Build jar file
```shell script
./gradlew shadowJar 
```
2. Build docker image
```shell script
docker build --tag good-readme .
```
3. Run docker image
```shell script
docker run -p 80:8080 --env GITHUB_TOKEN=<Github token> -d --rm good-readme
```

### From source code
1. Setup environment variables
Do either one of:
 - Change github.token and github.clientsecret in [application.conf](resources/application.conf)
 - Add `GITHUB_TOKEN` to environment variables
Build and run jar file
```shell script
./gradlew shadowJar && java -jar /build/libs/updatereadme.jar 
```
