# Git Nex Tag

## TODO

* use in Tekton Task
  * have an alternative output option
  * output to file -> can this be the output path?
  * if not, then we can use a bash task after to collect it

## Links

* https://dev.to/stack-labs/cli-applications-with-micronaut-and-picocli-4mc8
* https://e.printstacktrace.blog/building-stackoverflow-cli-with-java-11-micronaut-picocli-and-graalvm/
* https://mkyong.com/java/how-to-execute-shell-command-from-java/
* REGEX:
  * https://www.freeformatter.com/java-regex-tester.html#before-output
  * https://www.jrebel.com/blog/java-regular-expressions-cheat-sheet
* https://dimitri.codes/building-microservices-micronaut/

## GraalVM Image

* https://www.graalvm.org/22.0/docs/getting-started/container-images/
* https://github.com/graalvm/container/pkgs/container/graalvm-ce 
* https://containers.fan/posts/speed-up-maven-docker-builds-with-cache/
* https://docs.oracle.com/en/graalvm/enterprise/20/docs/reference-manual/native-image/NativeImageMavenPlugin/
* https://quarkus.io/guides/quarkus-runtime-base-image

```shell
docker pull ghcr.io/graalvm/graalvm-ce:ol9-java17-22.3.0-b2
```

```shell
docker run -it --rm docker run -it --rm  maven:3.8.7-amazoncorretto-19 bash
```

```dockerfile
FROM maven:3.8.7-amazoncorretto-19

```

```shell
docker run -it --rm --entrypoint bash ghcr.io/joostvdg/maven-graal-ce-build:0.3.0-m3.8-j19-g22.3
``` 

## Testing

```shell
docker run -it --rm --entrypoint bash ghcr.io/joostvdg/git-next-tag:0.1.0-rc6
``` 

```shell
docker run --rm ghcr.io/joostvdg/git-next-tag:0.1.0-rc6
``` 

```shell
docker run --rm -v $(pwd):/git ghcr.io/joostvdg/git-next-tag:0.1.0 -b "v0.2.*" -p /git
```