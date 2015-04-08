#/bin/bash
ant clean
find . -name '*.class' -type f -delete
rm -r publish
ant -Dargs=\"$@\"
