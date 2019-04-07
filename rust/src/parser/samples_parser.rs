use std::fs::File;
use std::str;
use std::io::{BufRead, BufReader};

#[derive(Debug)]
pub struct Header {
  start_date: String,
  end_date: String,
}

#[derive(Debug)]
pub struct Measure {
  name: String,
  typex: String,
  unitx: String,
}

#[derive(Debug)]
pub struct Sample {
  value: String,
  time: String,
  pub measure: usize,
}

pub struct SamplesParser {
  reader: BufReader<File>,
}

impl SamplesParser {
  pub fn new(file: File) -> SamplesParser {
    SamplesParser {
      reader: BufReader::new(file)
    }
  }

  pub fn parse_headers(&mut self) -> Result<Header, String> {
    let start_date = self.parse_header()?;
    let end_date = self.parse_header()?;
    return Ok(Header{start_date, end_date});
  }

  pub fn parse_measures(&mut self) -> Vec<Measure> {
    let mut measures = self.parse_only_measures();
    let types = self.parse_line(2, 0);
    let units = self.parse_line(2, 0);
    self.merge_types_units(&mut measures, types, units);
    return measures;
  }

  pub fn parse_samples(&mut self, size: usize, f: impl Fn(Vec<Sample>) -> ()) {
    loop {
      let mut samples = Vec::new();
      for _ in 0..500 {
        let arr = self.parse_line(0, 0);
        if arr.len() < 1 {
          f(samples);
          return;
        }
        for i in 2..arr.len() {
          if !arr[i].is_empty() && arr[i] != "NaN" && i < size {
            samples.push(Sample{value: arr[i].clone(), time: arr[1].clone(), measure: i - 2});
          }
        }
      }
      f(samples);
    }
  }

  fn parse_header(&mut self) -> Result<String, String> {
    let arr = self.parse_line(1, 1);
    if arr.len() < 1 {
      return Err("Bad line formating".to_string());
    }
    return Ok(arr[0].clone());
  }

  fn parse_only_measures(&mut self) -> Vec<Measure> {
    let arr = self.parse_line(2, 0);

    let measures = arr.into_iter()
      .map(|s| Measure{name: s, typex: String::new(), unitx: String::new()})
      .collect();

    let mut e = String::new();
    self.reader.read_line(&mut e).expect("Error reading empty line");

    return measures;
  }

  fn merge_types_units(&self, measures: &mut Vec<Measure>, types: Vec<String>, units: Vec<String>) {
    for (i, typex) in types.iter().enumerate() {
      measures[i].typex = typex.to_string();
    }
    for (i, unitx) in units.iter().enumerate() {
      measures[i].unitx = unitx.to_string();
    }
  }

  fn parse_line(&mut self, skip: usize, limit: usize) -> Vec<String> {
    let mut buffer = Vec::new();

    let num_bytes = self.reader.read_until(b'\n', &mut buffer)
      .expect("Error while reading");

    if num_bytes < 1 {
      return Vec::new();
    }

    let line = unsafe {
      str::from_utf8_unchecked(&buffer)
    };

    let arr: Vec<String> = line.split(';')
      .skip(skip)
      .enumerate()
      .filter(|(i, _)| {
        if limit > 0 {
          return i < &limit;
        }
        return true;
      })
      .map(|(_, e)| String::from_utf8(e.as_bytes().to_vec()).unwrap_or(String::new()))
      .collect();
  
    return arr;
  }
}
