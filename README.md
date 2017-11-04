## Simple opentracing example with jaeger
This example shows how a client and a server trace can be connected.

### Prerequisites
- Install gradle version > 4.2.1
- Install docker version > 1.12.6
- Install Eclipse version > 4.6.2
- Install browser

### Build and import project
- Clone repository with 'git clone https://github.com/rage5474/opentracing-helloworld.git'
- 'cd opentracing-helloworld'
- Create Eclipse project with 'gradle eclipse'
- Open Eclipse
- Import project by File->Import->General->Existing Projects into Workspace
- Select cloned folder

## Run example
- Open terminal and start tracing container with 'docker run --rm -p5775:5775/udp -p6831:6831/udp -p16686:1aegertracing/all-in-one:latest'
- Goto Eclipse
- Select 'HelloWorldService.java' in folder src/main/java/hello and run it with right click and Run as..->Java Application
- Select 'HelloWorld.java' in folder src/main/java/hello and run it with right click and Run as..->Java Application
- Open browser and enter url http://localhost:16686

