cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

GREEN="\e[1;32m"
RED="\e[31m"



echo -e '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests:"
for cas_de_test in ./src/test/deca/codegen/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/$filename.ass 2>/dev/null
    decac ./src/test/deca/codegen/$filename.deca  
    mv $filename.ass ./src/test/deca/codegen/
    if [ ! -f ./src/test/deca/codegen/$filename.ass ]; then
    echo -e "Fichier $filename.ass non généré."
    fi

    resultat=$(ima ./src/test/deca/codegen/$filename.ass) 
    rm -f ./src/test/deca/codegen/$filename.ass 

    # On code en dur la valeur attendue.
    attendu=ok

    if [ "$resultat" = "$attendu" ]; then
        echo "Tout va bien"
    else
        #echo "Résultat inattendu de ima:"
        echo 
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']
        echo -e ${GREEN}"$resultat"
    fi
done

for cas_de_test in ./src/test/deca/codegen/perf/provided/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/perf/provided/$filename.ass 2>/dev/null
    decac ./src/test/deca/codegen/perf/provided/$filename.deca  
    mv $filename.ass ./src/test/deca/codegen/perf/provided/
    if [ ! -f ./src/test/deca/codegen/perf/provided/$filename.ass ]; then
    echo -e "Fichier $filename.ass non généré."
    fi

    resultat=$(ima ./src/test/deca/codegen/perf/provided/$filename.ass) 
    rm -f ./src/test/deca/codegen/perf/provided/$filename.ass 

    # On code en dur la valeur attendue.
    attendu=ok

    if [ "$resultat" = "$attendu" ]; then
        echo "Tout va bien"
    else
        #echo "Résultat inattendu de ima:"
        echo 
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']
        echo -e ${GREEN}"$resultat"
    fi
done