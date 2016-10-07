#java -jar ~/rdf-contextualizer-0.0.1-SNAPSHOT.jar -zip -f mesh/nq -ext TTL -rep NG -spInitNum 1 -spInitStr mesh_R3 -shortenURI -dsName mesh > mesh_noinfer.txt
#java -jar ~/rdf-contextualizer-0.0.1-SNAPSHOT.jar -zip -f mesh/nq -ext TTL -rep NG -spInitNum 1 -spInitStr mesh_R3 -shortenURI -dsName mesh -infer mesh/onto > mesh_infer.txt

for name in ctd goa interpro mesh ncbigene ncbo orphanet pharmgkb taxonomy
do
rm -Rf "$name"/nq_*
java -jar ~/rdf-contextualizer-0.0.1-SNAPSHOT.jar -parallel 4 -zip -f "$name"/nq -ext TTL -rep NG -spInitNum 1 -spInitStr "$name"_R3 -shortenURI -dsName "$name" > log/"$name"_noinfer.txt
java -jar ~/rdf-contextualizer-0.0.1-SNAPSHOT.jar -parallel 4 -zip -f "$name"/nq -ext TTL -rep NG -spInitNum 1 -spInitStr "$name"_R3 -shortenURI -dsName "$name" -infer "$name"/onto > log/"$name"_infer.txt
done

for name in ctd goa interpro mesh orphanet pharmgkb taxonomy
do
java -jar ~/rdf-contextualizer-0.0.1-SNAPSHOT.jar -zip -f "$name"/nq -ext TTL -rep NG -spInitNum 1 -spInitStr "$name"_R3 -shortenURI -dsName "$name" > log/"$name"_noinfer.txt
java -jar ~/rdf-contextualizer-0.0.1-SNAPSHOT.jar -zip -f "$name"/nq -ext TTL -rep NG -spInitNum 1 -spInitStr "$name"_R3 -shortenURI -dsName "$name" -infer "$name"/onto > log/"$name"_infer.txt
done
for name in ctd goa interpro mesh ncbigene ncbo orphanet pharmgkb taxonomy
do
cd /semweb1/datasets/current
mkdir $name
cd $name
java -jar ~/rdf-contextualizer-0.0.1-SNAPSHOT.jar -dsName $name -url "http://download.bio2rdf.org/release/3/$name/"
mkdir nq
mkdir onto
cd nq
wget -i ../"$name"_links.txt

wget -O https://raw.githubusercontent.com/bio2rdf/bio2rdf-mapping/master/2/$name/bio2rdf_"$name"_sio_mapping.owl bio2rdf_"$name"_sio_mapping.owl || rm bio2rdf_"$name"_sio_mapping.owl
cd ../../
done

#wget -O https://raw.githubusercontent.com/bio2rdf/bio2rdf-mapping/master/2/$name/bio2rdf_"$name"_sio_mapping.owl bio2rdf_"$name"_sio_mapping.owl || rm bio2rdf_"$name"_sio_mapping.owl

for name in ctd goa interpro mesh ncbigene ncbo orphanet pharmgkb taxonomy
do
if [ -d "$name" ]; then
ls -lha $name
if [ -d $name/nq ]; then
ls -lha $name/nq/
fi
if [ -d $name/log ]; then
ls -lha $name/log/
fi
if [ -d $name/onto ]; then
ls -lha $name/onto/
fi
if [ -d $name/nq_converted_to_sp_inf_ttl ]; then
ls -lha $name/nq_converted_to_sp_inf_ttl/
fi
if [ -d $name/nq_converted_to_sp_ttl ]; then
ls -lha $name/nq_converted_to_sp_ttl/
fi
fi
done

# Check if the folder contains file
shopt -s nullglob dotglob     # To include hidden files
files=(./*)
if [ ${#files[@]} -gt 0 ]; then echo "huzzah"; fi

