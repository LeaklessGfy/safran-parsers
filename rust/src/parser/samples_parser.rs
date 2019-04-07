use std::fs::File;
use std::io::BufReader;
use crate::parser::{parse_line, read_empty_line};

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

  pub fn parse_measures(&mut self) -> Result<Vec<Measure>, String> {
    let mut measures = self.parse_only_measures()?;
    let types = parse_line(&mut self.reader, 2, 0)?;
    let units = parse_line(&mut self.reader, 2, 0)?;
    self.merge_types_units(&mut measures, types, units)?;
    return Ok(measures);
  }

  pub fn parse_samples(&mut self, size: usize, f: impl Fn(Vec<Sample>) -> ()) -> Result<i64, String> {
    let mut length = 0;
    loop {
      let mut samples = Vec::new();
      for _ in 0..500 {
        let arr = parse_line(&mut self.reader, 0, 0)?;
        if arr.len() < 1 {
          f(samples);
          return Ok(length);
        }
        for i in 2..arr.len() {
          if !arr[i].is_empty() && arr[i] != "NaN" && i < size {
            samples.push(Sample{value: arr[i].clone(), time: arr[1].clone(), measure: i - 2});
            length += 1;
          }
        }
      }
      f(samples);
    }
  }

  fn parse_header(&mut self) -> Result<String, String> {
    let arr = parse_line(&mut self.reader, 1, 1)?;
    if arr.len() < 1 {
      return Err("Bad line formating".to_string());
    }
    return Ok(arr[0].clone());
  }

  fn parse_only_measures(&mut self) -> Result<Vec<Measure>, String> {
    let arr = parse_line(&mut self.reader, 2, 0)?;

    let measures = arr.into_iter()
      .map(|s| Measure{name: s, typex: String::new(), unitx: String::new()})
      .collect();

    read_empty_line(&mut self.reader)?;

    return Ok(measures);
  }

  fn merge_types_units(&self, measures: &mut Vec<Measure>, types: Vec<String>, units: Vec<String>) -> Result<(), String> {
    if measures.len() != types.len() {
      return Err("Bad format : Types length isn't equal to measures length".to_string());
    }
    if measures.len() != units.len() {
      return Err("Bad format : Units length isn't equal to measures length".to_string());
    }
    for (i, typex) in types.iter().enumerate() {
      measures[i].typex = typex.to_string();
    }
    for (i, unitx) in units.iter().enumerate() {
      measures[i].unitx = unitx.to_string();
    }
    return Ok(());
  }
}
