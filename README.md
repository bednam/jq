# run tests
scala-cli test JqTest.test.scala

scala-cli test DownSpec.test.scala

# run 
scala-cli jq.test -- 'filter'

json1

json1

...

# package as native image
scala-cli package jq.scala --native-image --output jq

# supported features

## extract object
./jq '.key'

in: {"key": "value"}

out: "value"

## extract nested object
./jq '.key.nestedKey'

in: {"key": {"nestedKey": "value"}}

out: "value"

## extract element from array
./jq '.[0]'

in: [0, 1]

out: 0

## extract array
./jq '.key'

in: {"key": [1, 2]}

out: [1, 2]

## extract elements from array
./jq '.key[]'

in: {"key": [1, 2]}

out:\
    1\
    2

## extract values from all objects in array
./jq '.key[].keyInArray'

in: {"key": [{"keyInArray": "value1"}, {"keyInArray": "value2"}]}

out:\
    "value1"\
    "value2"

## return null instead of terminating program when key doesn't exist
./jq '.otherKey?'

in: {"key": "value"}

out: null