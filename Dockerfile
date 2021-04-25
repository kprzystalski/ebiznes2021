FROM ubuntu:18.04

ENV TZ=Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone 

RUN apt update && apt install -y build-essential unzip vim git curl wget zip

RUN apt-get update &&\
	apt-get upgrade -y &&\
    apt-get install -y  software-properties-common

# JS
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
RUN apt-get install -y nodejs
RUN npm install -g npm@latest

EXPOSE 8080
EXPOSE 5000
EXPOSE 9000


RUN useradd -ms /bin/bash kprzystalski
RUN adduser kprzystalski sudo

USER kprzystalski
WORKDIR /home/kprzystalski/
RUN curl -s "https://get.sdkman.io" | bash
RUN chmod a+x "/home/kprzystalski/.sdkman/bin/sdkman-init.sh"
RUN bash -c "source /home/kprzystalski/.sdkman/bin/sdkman-init.sh && sdk install java 8.0.272.hs-adpt"
RUN bash -c "source /home/kprzystalski/.sdkman/bin/sdkman-init.sh && sdk install sbt 1.4.8"
RUN bash -c "source /home/kprzystalski/.sdkman/bin/sdkman-init.sh && sdk install scala 2.12.13"

RUN git clone https://github.com/VundleVim/Vundle.vim.git ~/.vim/bundle/Vundle.vim
RUN git clone https://github.com/kprzystalski/vim && cp vim/.vimrc /home/kprzystalski/

RUN mkdir projekt
WORKDIR /home/kprzystalski/projekt/
#RUN bash -c "source /home/kprzystalski/.sdkman/bin/sdkman-init.sh && sbt new playframework/play-scala-seed.g8"

