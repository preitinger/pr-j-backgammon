#!/bin/sh


# BEGIN Folgendes jetzt in pr.backgammon.control.AnalyzeMatch
# gnome-terminal --profile=keep-open --title='Bitte testen und dann mit Strg-C abbrechen' --wait -- sh -c 'cd $WS/pr-home; npm run incVersionsBuildStart'

# echo "Fortfahren? (j/n)"
# read decision

# if [ "$decision" = "j" ]
# then
#     echo "then"
# else
#     echo "else"
#     exit 0
# fi
# END Folgendes jetzt in pr.backgammon.control.AnalyzeMatch


cd $WS/pr-home &&
git switch local &&
git stage local/lastVersion.txt &&
git commit -m 'update lastVersion.txt' &&
git switch -c for-common &&
git add public/gnubg/*.html public/gnubg/html-images/* &&
git stage app/_lib/both/version.ts public/gnubg/* public/gnubg/html-images/* &&
git commit -m 'add/update gnubg matches' &&
pr-local-scripts/script/rebase-and-merge-feature-branch.sh . for-common &&
cd ../pr-home_MAIN &&
git push &&

echo "Muesste nun auf vercel re-deployed werden..."


