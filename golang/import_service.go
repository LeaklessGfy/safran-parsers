package main

import (
	"io"
	"fmt"
	"net/http"
	"encoding/json"

	"github.com/influxdata/influxdb1-client/v2"
)

type ImportService struct {
	c client.Client
}

func HandleImport(w http.ResponseWriter, r *http.Request, message chan []byte) {
	importService, err := NewImportService()
	if err != nil {
		json.NewEncoder(w).Encode(Response{Err: true, Msg: err.Error()})
		return
	}
	samplesFile, _, err := r.FormFile("samples")	
	if err != nil {
		json.NewEncoder(w).Encode(Response{Err: true, Msg: err.Error()})
		return
	}
	defer samplesFile.Close()
	importService.importSamples(samplesFile)
	alarmsFile, _, err := r.FormFile("alarms")	
	if err == nil {
		defer alarmsFile.Close()
		importService.importAlarms(alarmsFile)
	}
}

func NewImportService() (*ImportService, error) {
	c, err := client.NewHTTPClient(client.HTTPConfig{
		Addr: "http://localhost:8086",
	})
	if err != nil {
		return nil, err
	}
	defer c.Close()

	return &ImportService{c}, nil
}

func (i ImportService) importSamples(samplesReader io.Reader) error {
	samplesParser := NewSamplesParser(samplesReader)
	_, err := samplesParser.ParseHeader()
	if err != nil {
		return err
	}
	measures, err := samplesParser.ParseMeasures()
	if err != nil {
		return err
	}
	samplesParser.ParseSamples(len(measures), func(samples []Sample) {
		fmt.Println(len(samples))
	})
	return nil
}

func (i ImportService) importAlarms(alarmsReader io.Reader) error {
	alarmsParser := NewAlarmsParser(alarmsReader)
	alarms, err := alarmsParser.ParseAlarms()
	if err != nil {
		return err
	}
	fmt.Println(alarms)
	return nil
}
