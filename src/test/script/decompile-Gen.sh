cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

GREEN="\e[1;32m"
RED="\e[31m"
Nbr=0
rm -r ./src/test/deca/codegen/TestsRes
mkdir ./src/test/deca/codegen/TestsRes

echo -e '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Gencode:"
for cas_de_test in ./src/test/deca/codegen/sansObjet/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/sansObjet/$filename.deca  > ./src/test/deca/codegen/sansObjet/$filename-decompile.deca
    decac ./src/test/deca/codegen/sansObjet/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/sansObjet/$filename-decompile.ass ]; then
    echo -e "Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/sansObjet/$filename-decompile.ass 
    echo "$resultat">./src/test/deca/codegen/TestsRes/$filename.res
    if [ "$resultat" 2>&1  ]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    elif [[ -z "$resultat" ]]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    else
        #echo "Résultat inattendu de ima:"
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
    fi
    Nbr=$(($Nbr + 1))
done
for cas_de_test in ./src/test/deca/codegen/objet/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/objet/$filename.deca  > ./src/test/deca/codegen/objet/$filename-decompile.deca
    decac ./src/test/deca/codegen/objet/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/objet/$filename-decompile.ass ]; then
    echo -e "Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/objet/$filename-decompile.ass 
    echo "$resultat">./src/test/deca/codegen/TestsRes/$filename.res
    if [ "$resultat" 2>&1  ]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    elif [[ -z "$resultat" ]]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    else
        #echo "Résultat inattendu de ima:"
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
    fi
    Nbr=$(($Nbr + 1))
done
for cas_de_test in ./src/test/deca/codegen/valid/provided/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/valid/provided/$filename.deca  > ./src/test/deca/codegen/valid/provided/$filename-decompile.deca
    decac ./src/test/deca/codegen/valid/provided/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/valid/provided/$filename-decompile.ass ]; then
    echo "Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/valid/provided/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/valid/provided/$filename-decompile.ass 

    echo "$resultat">./src/test/deca/codegen/TestsRes/$filename.res
    if [ "$resultat" 2>&1  ]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    elif [[ -z "$resultat" ]]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    else
        #echo "Résultat inattendu de ima:"
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
    fi
    Nbr=$(($Nbr + 1))
done
for cas_de_test in ./src/test/deca/codegen/perf/provided/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.ass 2>/dev/null
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.deca 2>/dev/null
    decac -p ./src/test/deca/codegen/perf/provided/$filename.deca  > ./src/test/deca/codegen/perf/provided/$filename-decompile.deca
    decac ./src/test/deca/codegen/perf/provided/$filename-decompile.deca
    if [ ! -f ./src/test/deca/codegen/perf/provided/$filename-decompile.ass ]; then
    echo -e "${RED}Fichier $filename.ass non généré."
    fi
    resultat=$(ima ./src/test/deca/codegen/perf/provided/$filename-decompile.ass)
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.deca
    rm -f ./src/test/deca/codegen/perf/provided/$filename-decompile.ass 
    echo "$resultat">./src/test/deca/codegen/TestsRes/$filename.res
    
    if [ "$resultat" 2>&1  ]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué ${RED}"
    elif [[ -z "$resultat" ]]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué ${RED}"
    else
        #echo "Résultat inattendu de ima:"
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu ${RED}"
    fi
    Nbr=$(($Nbr + 1))
done


echo
echo 
echo -e '\e[1;37m'['\e[1;34m'INFO'\e[1;37m']'\e[1;35m'"  Totale tests gencode : $Nbr "




