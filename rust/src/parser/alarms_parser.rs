use std::fs::File;
use std::io::{BufRead, BufReader};

#[derive(Debug)]
pub struct Alarm {
  level: i64,
  time: String,
  message: String,
}

pub struct AlarmsParser {
  reader: BufReader<File>,
}

impl AlarmsParser {
  pub fn new(file: File) -> AlarmsParser {
    AlarmsParser {
      reader: BufReader::new(file)
    }
  }
}
