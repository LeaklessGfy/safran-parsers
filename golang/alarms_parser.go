package main

import (
	"bufio"
	"errors"
	"io"
	"strconv"
	"strings"
)

type AlarmsParser struct {
	scanner *bufio.Scanner
}

type Alarm struct {
	Time    string
	Level   int
	Message string
}

// NewAlarmsParser create a Sample Parser with the scanner
func NewAlarmsParser(reader io.Reader) *AlarmsParser {
	return &AlarmsParser{bufio.NewScanner(reader)}
}

// ParseAlarms parse alarms in the file
func (p AlarmsParser) ParseAlarms() ([]*Alarm, error) {
	var alarms []*Alarm
	for p.scanner.Scan() {
		line := p.scanner.Text()
		if len(line) < 1 {
			return alarms, nil
		}
		arr := strings.Split(line, separator)
		if len(arr) < 3 {
			return alarms, errors.New("")
		}
		time := arr[0]
		level, err := strconv.Atoi(arr[1])
		if err != nil {
			return alarms, err
		}
		alarms = append(alarms, &Alarm{Time: time, Level: level, Message: arr[2]})
	}
	return alarms, nil
}
