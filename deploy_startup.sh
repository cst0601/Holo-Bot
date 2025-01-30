# deploy_startup.sh
# 
# This script is for running environment and will: 
# 1. stop all running docker 
# 2. remove all containers
# 3. pull latest version of the image 
# 4. run the latest image 
# 
# Required argument: the url to docker image. 
#
docker stop $(docker ps -aq)
docker rmi $(docker images -aq)
docker pull $1 
docker run -d --rm --name holo-bot $1 