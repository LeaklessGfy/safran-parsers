package main

import (
	"io"
	"bufio"
	"errors"
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
func NewSamplesParser(reader io.Reader) *SamplesParser {
	return &SamplesParser{bufio.NewScanner(reader)}
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
func (p SamplesParser) ParseMeasures() ([]*Measure, error) {
	measures, err := p.parseMeasures()
	if err != nil {
		return nil, err
	}
	types, err := parseLine(p.scanner, 2, 0)
	if err != nil {
		return nil, err
	}
	units, err := parseLine(p.scanner, 2, 0)
	if err != nil {
		return nil, err
	}
	err = p.mergeTypesUnits(measures, types, units)
	if err != nil {
		return nil, err
	}
	return measures, nil
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
	arr, err := parseLine(p.scanner, 1, 1)
	if err != nil {
		return "", err
	}
	if len(arr) < 1 {
		return "", errors.New("")
	}
	return arr[0], nil
}

func (p SamplesParser) parseMeasures() ([]*Measure, error) {
	arr, err := parseLine(p.scanner, 2, 0)
	if err != nil {
		return nil, err
	}
	var measures []*Measure
	for _, m := range arr {
		measures = append(measures, &Measure{Name: m})
	}
	p.scanner.Scan()
	return measures, nil
}

func (p SamplesParser) mergeTypesUnits(measures []*Measure, types, units []string) error {
	if len(types) != len(measures) {
		return errors.New("Types length != measures length")
	}
	if len(units) != len(measures) {
		return errors.New("Units length != measures length")
	}
	for i, typex := range types {
		measures[i].Typex = typex
	}
	for i, unitx := range units {
		measures[i].Unitx = unitx
	}
	return nil
}
