# GoodReadMe
The app update version of library in your ReadMe file.

## How it's work
App receive event about new release -> App fork your repo -> App create pull request with change in your ReadMe file.

## Setup
### Use our github action (InProgress)

### By GitHub WebHook (Recommended)
Go to Repository Setting -> WebHooks -> Add webhook 
 - Payload URL: `http://159.203.181.74:8080/checkMe`
 - Content type: `application/json`
 - Which events would you like to trigger this webhook?: `everything`
 
### Manually
Call server manually
```http request
POST http://159.203.181.74:8080/checkMe
Content-Type: application/json

{
  "owner": "<Owner name>",
  "repo": "<Repo name>"
}
```

## Setup app for self host usage.
### Easy run (DockerHub)
```shell script
docker pull vovochkastelmashchuk/good-readme:1.0
docker run -p 8080:8080 --env GIT_HUB_TOKEN=<Github token> --rm vovochkastelmashchuk/good-readme:1.0
```

### Run from source code with docker
Change github.token in [application.conf](resources/application.conf)
Build jar file
```shell script
./gradlew shadowJar 
```
Build docker image
```shell script
docker build --tag good-readme .
```
Run docker image
```shell script
docker run -p 8080:8080 --env GIT_HUB_TOKEN=<Your github token> --rm good-readme
```

### Run from source code
Change github.token in [application.conf](resources/application.conf)
Build and run jar file
```shell script
./gradlew shadowJar && java -jar /build/libs/updatereadme.jar 
```
  

