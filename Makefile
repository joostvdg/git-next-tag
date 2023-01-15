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
