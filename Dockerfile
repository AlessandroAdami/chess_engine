FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# Install prerequisites
RUN apt-get update && apt-get install -y \
    build-essential cmake g++ git \
    python3 python3-pip python3-venv \
    wget curl ca-certificates gnupg lsb-release software-properties-common \
    libgtest-dev libsfml-dev \
    && rm -rf /var/lib/apt/lists/*

# Add LLVM repo and install clang/clang-tidy 16
RUN wget https://apt.llvm.org/llvm.sh \
    && chmod +x llvm.sh \
    && ./llvm.sh 16 all \
    && rm llvm.sh

# Make clang, clang++ and clang-tidy-16 the defaults
RUN update-alternatives --install /usr/bin/clang clang /usr/bin/clang-16 100 \
 && update-alternatives --install /usr/bin/clang++ clang++ /usr/bin/clang++-16 100 \
 && update-alternatives --install /usr/bin/clang-tidy clang-tidy /usr/bin/clang-tidy-16 100

# Build GoogleTest libraries from source (provided by apt)
RUN cd /usr/src/gtest && \
    cmake . && \
    make && \
    cp lib/*.a /usr/lib

WORKDIR /app

# Default environment variables for X11 forwarding
ENV DISPLAY=:0
ENV QT_X11_NO_MITSHM=1

# Mount X11 socket and allow GUI apps
VOLUME ["/tmp/.X11-unix"]

CMD ["/bin/bash"]
