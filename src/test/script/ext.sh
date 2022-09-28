cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

GREEN="\e[1;32m"
RED="\e[31m"
Nbr=0
rm -r ./src/test/deca/codegen/TestsRes
mkdir ./src/test/deca/codegen/TestsRes

echo  -e '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests Extention Byte:"
for cas_de_test in ./src/test/deca/codegen/sansObjet/*.deca
do 
    filename=$(echo ${cas_de_test} | xargs basename | cut -d '.' -f1 )
    rm -f ./src/test/deca/codegen/sansObjet/$filename.class 2>/dev/null
    decac -j ./src/test/deca/codegen/sansObjet/$filename.deca
    if [ ! $filename.class ]; then
    echo  "Fichier $filename.class non généré."
    fi
    resultat=$(java $filename)
    rm -f $filename.class
    
    if [ "$resultat" 2>&1  ]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    elif [ -z "$resultat" ]; then
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${GREEN}$filename: Test effectué"
    else
        #echo "Résultat inattendu de Byte:"
        echo -e '\e[1;37m'['\e[1;34m'TEST'\e[1;37m'] "${RED}$filename: Résultat inattendu"
    fi
    Nbr=$(($Nbr + 1))
done


echo
echo 
echo -e '\e[1;37m'['\e[1;34m'INFO'\e[1;37m']'\e[1;35m'"  Totale tests gencode : $Nbr "