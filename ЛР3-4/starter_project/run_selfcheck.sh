#!/bin/sh
set -eu
cd -- "$(dirname -- "$0")"
java -Dfile.encoding=UTF-8 -cp OrderCalculator.jar ru.edu.pr01.SelfCheck
java -Dfile.encoding=UTF-8 -cp OrderCalculator.jar ru.edu.pr01.BoundaryCheck
