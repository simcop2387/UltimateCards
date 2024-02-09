FROM debian:bookworm-backports

# This is a docker container for building the 

RUN apt update && apt install -y maven openjdk-17-jdk-headless
