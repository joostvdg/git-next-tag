test:
	mvnd test -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -T 1C -e
verify:
	mvnd clean verify -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -T 1C -e
compile:
	mvnd compile -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -T 1C -e
install:
	mvnd install -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -T 1C -e

release:
	mvnd jreleaser:full-release -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -T 1C -e

package:
	mvnd clean package -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -T 1C -e

npackage:
	mvnd package -Dpackaging=native-image -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -T 1C -e

run: package
	java -jar target/git-next-tag-0.1.jar -v -b "v0.1.*"

build-image-push:
	docker buildx build ./build-image -f build-image/Dockerfile --platform linux/amd64,linux/arm64 --tag ghcr.io/joostvdg/maven-graal-ce-build:0.4.0-m3.8-j19-g22.3 --push

build-image-alpine-push:
	docker buildx build ./build-image -f build-image-alpine/Dockerfile --platform linux/amd64 --tag ghcr.io/joostvdg/maven-graal-ce-build-alpine:0.1.0-m3.8-j19-g22.3 --push

image-push:
	docker buildx build . --platform linux/amd64 --tag ghcr.io/joostvdg/git-next-tag:0.1.0 --push