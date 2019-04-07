mod parser;

use std::fs::File;
use parser::samples_parser::SamplesParser;
use parser::alarms_parser::AlarmsParser;

fn main() {
  let samples_file = File::open("../testfile.csv").expect("File not found");
  let mut samples_parser = SamplesParser::new(samples_file);
  let headers = samples_parser.parse_headers().expect("Bad headers format");
  let measures = samples_parser.parse_measures().expect("Bad measures format");

  println!("{:?}", headers);
  println!("{}", measures.len());

  samples_parser.parse_samples(measures.len(), |samples| {
    let sample = samples.first().unwrap();
    println!("{:?}", sample);
  }).expect("Bad samples format");

  let alarms_file = File::open("../event.csv").expect("Alarms file not found");
  let mut alarms_parser = AlarmsParser::new(alarms_file);
  let alarms = alarms_parser.parse_alarms();

  println!("{:?}", alarms); 
}
