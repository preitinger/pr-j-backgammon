#!/bin/sh

echo $@ >testcommands.gnubg

cat testcommands.gnubg

gnubg -tc testcommands.gnubg
 
