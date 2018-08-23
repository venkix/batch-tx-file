#Build an image and push to a repository (e.g. dockerhub):

mvn compile jib:build

#Build an image to docker daemon:

mvn compile jib:dockerBuild

#Build an image tarball:

mvn compile jib:buildTar
(afterwards it can be imported using docker command, e.g., docker load --input target/jib-image.tar)

