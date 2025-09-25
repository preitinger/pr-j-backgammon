#!/bin/sh


cd $(dirname $0)

echo 'Build ...' &&
./build.sh &&
echo 'Run ...' &&
./run.sh

