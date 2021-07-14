
- No WSL mudar diretório para "Websandbox-main" do nosso projecto :
cd /diretorio/do/grupo6/WebSandbox-main

Passo 1)
criar uma rede no docker com:
docker network create grupo6

mkdir /home/user_name/data


Postgres Build:
docker build -f postgres.dockerfile --tag postgres /mnt/e/Users/Cax/Documents/Cax_Universidade/3ºAno/SDP/WebSandbox-main

Postgres RUN:
docker run --rm --network grupo6 -e POSTGRES_PASSWORD=12345678 -v /home/cax/data:/var/lib/postgresql/data --name postgres -it postgres


Passo 2)
Abrir outro terminal/CMD/PowerShell:

cd /diretorio/do/grupo6/WebSandbox-main

Wildfly Build:
docker pull asabino/wildfly:21.0.2.Final-jdk15

WildFly RUN:
docker run --rm --network grupo6 -p 8080:8080 -p 9990:9990 -v $(pwd)/target:/opt/jboss/wildfly/standalone/deployments --name wildfly asabino/wildfly:21.0.2.Final-jdk15


Passo 3)
Abrir outro terminal/CMD/PowerShell:
cd E:\Users\Cax\Documents\Cax_Universidade\3ºAno\SDP\WebSandbox-main
mvn clean package


Testar com os exemplos que estão no relatório ou no mínimo seguir a mesma formatação dos mesmos.
