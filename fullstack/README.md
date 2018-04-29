# demo
This application was generated using JHipster 4.14.1, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.14.1](http://www.jhipster.tech/documentation-archive/v4.14.1).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.
2. [Yarn][]: We use Yarn to manage Node dependencies.
   Depending on your system, you can install Yarn either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    yarn install

We use yarn scripts and [Webpack][] as our build system.


Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    yarn start

[Yarn][] is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `yarn update` and `yarn install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `yarn help update`.

The `yarn run` command will list all of the scripts available to run for this project.

### Service workers

Service workers are commented by default, to enable them please uncomment the following code.

* The service worker registering script in index.html

```html
<script>
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker
        .register('./sw.js')
        .then(function() { console.log('Service Worker Registered'); });
    }
</script>
```

Note: workbox creates the respective service worker and dynamically generate the `sw.js`

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

    yarn add --exact leaflet

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

    yarn add --dev --exact @types/leaflet

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Edit [src/main/webapp/app/vendor.ts](src/main/webapp/app/vendor.ts) file:
~~~
import 'leaflet/dist/leaflet.js';
~~~

Edit [src/main/webapp/content/css/vendor.css](src/main/webapp/content/css/vendor.css) file:
~~~
@import '~leaflet/dist/leaflet.css';
~~~
Note: there are still few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using angular-cli

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

    ng generate component my-component

will generate few files:

    create src/main/webapp/app/my-component/my-component.component.html
    create src/main/webapp/app/my-component/my-component.component.ts
    update src/main/webapp/app/app.module.ts


## Building for production

To optimize the demo application for production, run:

    ./mvnw -Pprod clean package

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar target/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./mvnw clean test

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

    yarn test


### Other tests

Performance tests are run by [Gatling][] and written in Scala. They're located in [src/test/gatling](src/test/gatling) and can be run with:

    ./mvnw gatling:execute

For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a mysql database in a docker container, run:

    docker-compose -f src/main/docker/mysql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/mysql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./mvnw verify -Pprod dockerfile:build

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Execution

You can run your application locally with

    ./mvnw
    
If you want to use a production database and a Terracotta cluster in Docker 

    docker-compose -f src/main/docker/mysql.yml up -d
    docker-compose -f src/main/docker/terracotta-server-single.yml up -d
    ./mvnw -Pprod

Or if you want everything in Docker

    ./mvnw package -Pprod dockerfile:build
    docker-compose -f src/main/docker/app.yml up -d
    docker-compose -f src/main/docker/app.yml ps

The last line will allow you to see on which port the app port was forwarded locally (e.g. 32769 below).

             Name                        Command               State                 Ports               
    ----------------------------------------------------------------------------------------------------
    demo-mysql                 /docker-entrypoint.sh mysql     Up      0.0.0.0:3306->3306/tcp            
    demo-terracotta-server1   /bin/sh -c sed -i -r ' s/T ...   Up      0.0.0.0:32801->9410/tcp, 9430/tcp 
    demo-terracotta-server2   /bin/sh -c sed -i -r ' s/T ...   Up      0.0.0.0:32800->9410/tcp, 9430/tcp 
    docker_demo-app_1         /bin/sh -c echo "The appli ...   Up      0.0.0.0:32802->8080/tcp      

You wanna "scale"? Sure, go ahead. Here we go:
       
    $ docker-compose -f src/main/docker/app.yml scale demo-app=3
    $ docker-compose -f src/main/docker/app.yml ps
    Name                       Command               State                Ports               
    --------------------------------------------------------------------------------------------------
    demo-mysql                /docker-entrypoint.sh mysql   Up      0.0.0.0:5432->3306/tcp           
    demo-terracotta-server1   /bin/sh -c sed -i -r ' s/T ...   Up      0.0.0.0:32801->9410/tcp, 9430/tcp 
    demo-terracotta-server2   /bin/sh -c sed -i -r ' s/T ...   Up      0.0.0.0:32800->9410/tcp, 9430/tcp 
    docker_demo-app_1         /bin/sh -c echo "The appli ...   Up      0.0.0.0:9000->8080/tcp           
    docker_demo-app_2         /bin/sh -c echo "The appli ...   Up      0.0.0.0:32769->8080/tcp          
    docker_demo-app_3         /bin/sh -c echo "The appli ...   Up      0.0.0.0:32768->8080/tcp  
    
Feeling curious? Nice, go and have a look at src/main/docker, everything is there.

You're more than welcome to trigger failovers killing your active Terracotta server :

    docker-compose -f src/main/docker/app.yml stop demo-terracotta-server1

You should see in the logs :

    demo-terracotta-server2    | [TC] 2017-02-06 16:20:00,833 INFO - Terracotta Server instance has started up as ACTIVE node on 0:0:0:0:0:0:0:0:9410 successfully, and is now ready for work.

And of course, the webapp behaving normally!
 
### Docker swarm mode (1.12+) and overlay network (theory, not verified yet)  

After setting up a Docker swarm with several nodes (docker hosts), the idea is to have :

* a mysql service, replicas=1
* a terracotta-server service, replicas=1
* a demo-app service, replicas=n 

First, create a common network :

    docker network create -d overlay my-network

Start the mysql service :

    docker service create --name=mysql --replicas=1--network=my-network mysql:5.7.20

Start the terracotta-server service :

    docker service create --name=terracotta-server --replicas=1 --network=my-network terracotta/terracotta-server-oss:5.4.0

Start several webapp instances :

    docker service create --name=demo-app --replicas=5 --network=my-network demo
    
Verify everything is fine with :
    
    docker service ls

## Kubernetes deployment

With a correctly sized Kubernetes cluster :

    kubectl apply -f src/main/kubernetes
    
And open the app, play around, start scaling, etc.    
