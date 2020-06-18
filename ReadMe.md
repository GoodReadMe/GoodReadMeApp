# GoodReadMe
The app update version of library in your ReadMe file.

## How it's work
App receive event about new release -> App fork your repo -> App create pull request with change in your ReadMe file.

## Setup
### Use our github action (InProgress)

### By GitHub WebHook (Recommended)
Go to Repository Setting -> WebHooks -> Add webhook 
 - Payload URL: `http://goodreadme.androidstory.dev:8080/checkMe/byReleaseWebHook`
 - Content type: `application/json`
 - Which events would you like to trigger this webhook?: `everything`
 - **Only for self host usage** Client secret : <Your secret>
 
### Manually
**X-Hub-Signature required only for self host usage** 
Call server manually
```http request
POST http://goodreadme.androidstory.dev:8080/checkMe/byRepoDetails
Content-Type: application/json
X-Hub-Signature: <Your secret>

{
  "owner": "<Owner name>",
  "repo": "<Repo name>"
}
```
or
```http request
POST http://goodreadme.androidstory.dev:8080/checkMe/byRepoFullName
Content-Type: application/json
X-Hub-Signature: <Your secret>

{
  "fullName": "<Owner name>/<Repo name>"
}
```


## Setup app for self host usage.
### Easy run (DockerHub)
```shell script
docker pull vovochkastelmashchuk/good-readme:1.0
docker run -p 8080:8080 --env GITHUB_TOKEN=<Github token> --env GITHUB_TOKEN=<Client secret> --rm vovochkastelmashchuk/good-readme:1.0
```

### Run from source code with docker
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
docker run -p 8080:8080 --env GITHUB_TOKEN=<Github token> --env GITHUB_TOKEN=<Client secret> --rm good-readme
```

### Run from source code
1. Setup environment variables
Choose one:
 - Change github.token and github.clientsecret in [application.conf](resources/application.conf)
 - Add `GITHUB_TOKEN` and `CLIENT_SECRET` to environment variable
Build and run jar file
```shell script
./gradlew shadowJar && java -jar /build/libs/updatereadme.jar 
```
