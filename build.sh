cd $(dirname $0)

paths="pr pr/backgammon pr/backgammon/ui pr/backgammon/control pr/backgammon/spin pr/backgammon/spin/ui pr/backgammon/gnubg"

args=""
for item in $paths
do
    args="${args:+$args }${item}/*.java"
done

echo "args: ${args}"
args2=$(find pr -name '*.java')
echo "args2: ${args2}"

javac -cp ~/javaee/glassfish5/glassfish/modules/javax.json.jar $args2

