# Git Nex Tag

## TODO

* build native package for other architecture
* build and publish multi-arch OCI image
* use in Tekton Task

## Links

* https://dev.to/stack-labs/cli-applications-with-micronaut-and-picocli-4mc8
* https://e.printstacktrace.blog/building-stackoverflow-cli-with-java-11-micronaut-picocli-and-graalvm/
* https://mkyong.com/java/how-to-execute-shell-command-from-java/
* REGEX:
  * https://www.freeformatter.com/java-regex-tester.html#before-output
  * https://www.jrebel.com/blog/java-regular-expressions-cheat-sheet

## GraalVM Image

* https://www.graalvm.org/22.0/docs/getting-started/container-images/
* https://github.com/graalvm/container/pkgs/container/graalvm-ce 
* https://containers.fan/posts/speed-up-maven-docker-builds-with-cache/

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
docker run -it --rm caladreas/maven-graal-ce-build:0.1.0-m3.8-j19-g22.3 bash
```