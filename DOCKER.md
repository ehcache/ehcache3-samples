Docker instructions
====

Docker and docker compose instructions provided by JHipster
---
On a single docker host ([such as Docker 4 mac or Docker 4 Windows](https://www.docker.com/products/docker)), or a pre 1.12 swarm, let's suppose you want to deploy your db, your terracotta server and several instances of your webapp

First,[read the jhipster doc](https://jhipster.github.io/docker-compose/); that basically means :

Build your app, and its docker image :

    ./mvnw package -Pprod docker:build
    
Start everything up :
    
    docker-compose -f src/main/docker/app.yml up

And hit [http://localhost:9000](http://localhost:9000)
    
So you wanna "scale" ? sure, go ahead, but before, make sure to remove the port binding in app.yml for demo-app, or make sure that each container will be started ona  different swarm node, or else you'll hit :
    
    $ docker-compose -f src/main/docker/app.yml  scale demo-app=3
    WARNING: The "demo-app" service specifies a port on the host. If multiple containers for this service are created on a single host, the port will clash.
    Creating and starting docker_demo-app_2 ... error
    Creating and starting docker_demo-app_3 ... error

let's try again without port clashing :
       
    $ docker-compose -f src/main/docker/app.yml  scale demo-app=3
    $ docker-compose -f src/main/docker/app.yml ps
             Name                       Command               State                Ports               
    --------------------------------------------------------------------------------------------------
    demo-postgresql          /docker-entrypoint.sh postgres   Up      0.0.0.0:5432->5432/tcp           
    demo-terracotta-server   /bin/sh -c sed -i -r 's/OF ...   Up      0.0.0.0:9510->9510/tcp, 9530/tcp 
    docker_demo-app_1        /bin/sh -c echo "The appli ...   Up      0.0.0.0:9000->8080/tcp           
    docker_demo-app_2        /bin/sh -c echo "The appli ...   Up      0.0.0.0:32769->8080/tcp          
    docker_demo-app_3        /bin/sh -c echo "The appli ...   Up      0.0.0.0:32768->8080/tcp  
    
Feeling curious ? Nice, go and have a look at src/main/docker, everything is there.

    


Docker swarm mode (1.12+) and overlay network (theory, not verified yet)  
---

After setting up a Docker swarm with several nodes (docker hosts), the idea is to have :

* a postgresql service, replicas=1
* a terracotta-server service, replicas=1
* a demo-app service, replicas=n 

First, create a common network :

    docker network create -d overlay my-network

Start the postgresql service :

    docker service create --name=postgresql --replicas=1--network=my-network postgres:9.5.4

Start the terracotta-server service :

    docker service create --name=terracotta-server --replicas=1 --network=my-network anthonydahanne/terracotta-server-oss:5.0.0

Start several webapp instances :

    docker service create --name=demo-app --replicas=5 --network=my-network demo
    
Verify everything is fine with :
    
    docker service ls
    

