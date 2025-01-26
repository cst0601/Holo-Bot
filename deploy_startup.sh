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
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker pull $1 
docker run --name holo-bot $1 