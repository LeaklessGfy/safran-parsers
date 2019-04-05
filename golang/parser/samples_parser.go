package parser

import (
	"bufio"
	"errors"
	"os"
	"strings"
)

type SamplesParser struct {
	scanner *bufio.Scanner
}

type Header struct {
	StartDate string
	EndDate   string
}

type Measure struct {
	Name  string
	Typex string
	Unitx string
}

type Sample struct {
	Value   string
	Time    string
	Measure int
}

const offset = 2
const separator = ";"
const nan = "NaN"

// NewSamplesParser create a Sample Parser with the scanner
func NewSamplesParser(file *os.File) *SamplesParser {
	return &SamplesParser{bufio.NewScanner(file)}
}

// ParseHeader parse the start and end date of the file
func (p SamplesParser) ParseHeader() (*Header, error) {
	startDate, err := p.parseDate()
	if err != nil {
		return nil, err
	}
	endDate, err := p.parseDate()
	if err != nil {
		return nil, err
	}
	return &Header{startDate, endDate}, nil
}

// ParseMeasures parse the measures of the file
func (p SamplesParser) ParseMeasures() []*Measure {
	measures := p.parseMeasures()
	types := p.parseTypesUnits()
	units := p.parseTypesUnits()
	p.mergeTypesUnits(measures, types, units)
	return measures
}

// ParseSamples parse the samples of the file
func (p SamplesParser) ParseSamples(size int, executor func([]Sample)) {
	for true {
		var samples []Sample
		for n := 0; n < 500; n++ {
			if !p.scanner.Scan() {
				executor(samples)
				return
			}
			line := p.scanner.Text()
			arr := strings.Split(line, separator)
			for i := 2; i < len(arr); i++ {
				if len(arr[i]) > 0 && arr[i] != nan && i < size {
					samples = append(samples, Sample{Value: arr[i], Time: arr[1], Measure: i - offset})
				}
			}
		}
		executor(samples)
	}
}

func (p SamplesParser) parseDate() (string, error) {
	arr := p.parseLine(1, 1)
	if len(arr) < 1 {
		return "", errors.New("")
	}
	return arr[0], nil
}

func (p SamplesParser) parseMeasures() []*Measure {
	arr := p.parseLine(2, 0)
	var measures []*Measure
	for _, m := range arr {
		measures = append(measures, &Measure{Name: m})
	}
	p.scanner.Scan()
	return measures
}

func (p SamplesParser) parseTypesUnits() []string {
	return p.parseLine(2, 0)
}

func (p SamplesParser) mergeTypesUnits(measures []*Measure, types, units []string) {
	for i, typex := range types {
		measures[i].Typex = typex
	}
	for i, unitx := range units {
		measures[i].Unitx = unitx
	}
}

func (p SamplesParser) parseLine(skip int, limit int) []string {
	var arr []string
	p.scanner.Scan()
	line := p.scanner.Text()
	if len(line) < 1 {
		return arr
	}
	tmp := strings.Split(line, separator)
	lgt := skip + limit
	if limit < 1 {
		lgt = len(tmp)
	}
	return tmp[skip:lgt]
}
