mod parser;

use std::fs::File;
use parser::samples_parser::SamplesParser;
use parser::alarms_parser::AlarmsParser;

fn main() {
  let file = File::open("../testfile.csv").expect("File not found");
  let mut samples_parser = SamplesParser::new(file);

  let headers = samples_parser.parse_headers().expect("Bad token headers");
  let measures = samples_parser.parse_measures();

  println!("{:?}", headers);
  println!("{}", measures.len());

  samples_parser.parse_samples(measures.len(), |samples| {
    let sample = samples.first().unwrap();
    println!("{:?}", sample);
  });

  let alarms_file = File::open("../events.csv").expect("Alarms file not found");
  let mut alarms_parser = AlarmsParser::new(alarms_file);
  let alarms = alarms_parser.parse_alarms();

  println!("{:?}", alarms); 
}
