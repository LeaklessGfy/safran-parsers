package main

import (
	"fmt"
	"log"
	"net/http"
	"encoding/json"
)

type Response struct {
	Err bool   `json:"err"`
	Msg string `json:"msg"`
}

type Server struct {
	message chan []byte
}

func (s Server) indexHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hi there, I love %s!", r.URL.Path[1:])
}

func (s Server) uploadHandler(w http.ResponseWriter, r *http.Request) {
	r.ParseMultipartForm(32 << 20)
	go HandleImport(w, r, s.message)
	json.NewEncoder(w).Encode(Response{Err: false, Msg: "success"})
}

func (s Server) eventsHandler(w http.ResponseWriter, r *http.Request) {
	flusher, ok := w.(http.Flusher)
	if !ok {
		http.Error(w, "Streaming unsupported!", http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "text/event-stream")
	w.Header().Set("Cache-Control", "no-cache")
	w.Header().Set("Connection", "keep-alive")
	w.Header().Set("Access-Control-Allow-Origin", "*")
	for {
		fmt.Fprintf(w, "data: %s\n\n", <-s.message)
		flusher.Flush()
	}
}

func main() {
	server := Server{message: make(chan []byte)}
	http.HandleFunc("/", server.indexHandler)
	http.HandleFunc("/upload", server.uploadHandler)
	http.HandleFunc("/events", server.eventsHandler)
	log.Fatal(http.ListenAndServe(":8088", nil))
}
