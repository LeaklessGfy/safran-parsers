package main

import (
	"fmt"
	"log"
	"os"

	"github.com/leaklessgfy/safran-parser-golang/parser"
)

func main() {
	file, err := os.Open("C:\\Users\\vince\\Documents\\parser\\testfile.csv")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	sampleParser := parser.NewSamplesParser(file)

	_, err = sampleParser.ParseHeader()
	if err != nil {
		log.Fatal(err)
	}
	measures := sampleParser.ParseMeasures()
	fmt.Println(len(measures))
	sampleParser.ParseSamples(len(measures), func(samples []parser.Sample) {
		fmt.Println(samples[0], measures[samples[0].Measure])
	})
}
