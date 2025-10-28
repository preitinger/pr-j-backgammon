cd $(dirname $0)

paths="pr pr/backgammon pr/backgammon/control pr/backgammon/gnubg pr/backgammon/gnubg/model pr/backgammon/jokers pr/backgammon/jokers/control pr/backgammon/jokers/model pr/backgammon/jokers/view pr/backgammon/model pr/backgammon/spin pr/backgammon/spin/control pr/backgammon/spin/control/workers pr/backgammon/spin/model pr/backgammon/spin/templatesearchers pr/backgammon/spin/trackmove pr/backgammon/spin/view pr/backgammon/view pr/control pr/cutscreenshot pr/cv pr/http pr/model pr/res pr/res/board pr/res/gnubg pr/view"

args=""
for item in $paths
do
    args="${args:+$args }${item}/*.java"
done

echo "args: ${args}"
args2=$(find pr -name '*.java')
echo "args2: ${args2}"

javac -cp ~/javaee/glassfish5/glassfish/modules/javax.json.jar:lib/opencv-4130.jar:. $args2

