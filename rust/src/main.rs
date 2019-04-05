use std::fs::File;
use std::str;
use std::io::{BufRead, BufReader};

#[derive(Debug)]
struct Header {
  start_date: String,
  end_date: String,
}

#[derive(Debug)]
struct Measure {
  name: String,
  typex: String,
  unitx: String,
}

#[derive(Debug)]
struct Sample {
  value: String,
  time: String,
  measure: usize,
}

fn main() {
  let file = File::open("../testfile.csv").expect("File not found");
  let mut reader = BufReader::new(file);
  let headers = parse_headers(&mut reader).expect("Bad token headers");
  let measures = parse_measures(&mut reader);

  println!("{:?}", headers);
  println!("{}", measures.len());

  parse_samples(&mut reader, measures.len(), |samples| -> () {
    let sample = samples.first().unwrap();
    println!("{:?}", sample);
    println!("{:?}", measures[sample.measure]);
  });
}

fn parse_headers(reader: &mut BufReader<File>) -> Result<Header, ()> {
  let start_date = parse_header(reader)?;
  let end_date = parse_header(reader)?;
  return Ok(Header{start_date, end_date});
}

fn parse_header(reader: &mut BufReader<File>) -> Result<String, ()> {
  let arr = parse_line(reader, 1, 1);
  if arr.len() < 1 {
    return Err(());
  }
  return Ok(arr[0].clone());
}

fn parse_measures(reader: &mut BufReader<File>) -> Vec<Measure> {
  let mut measures = parse_only_measures(reader);
  let types = parse_types_units(reader);
  let units = parse_types_units(reader);
  merge_types_units(&mut measures, types, units);
  return measures;
}

fn parse_only_measures(reader: &mut BufReader<File>) -> Vec<Measure> {
  let arr = parse_line(reader, 2, 0);

  let measures = arr.into_iter()
    .map(|s| Measure{name: s, typex: String::new(), unitx: String::new()})
    .collect();

  let mut e = String::new();
  reader.read_line(&mut e).expect("Error reading empty line");

  return measures;
}

fn parse_types_units(reader: &mut BufReader<File>) -> Vec<String> {
  return parse_line(reader, 2, 0);
}

fn merge_types_units(measures: &mut Vec<Measure>, types: Vec<String>, units: Vec<String>) {
  for (i, typex) in types.iter().enumerate() {
    measures[i].typex = typex.to_string();
  }
  for (i, unitx) in units.iter().enumerate() {
    measures[i].unitx = unitx.to_string();
  }
}

fn parse_samples(reader: &mut BufReader<File>, size: usize, f: impl Fn(Vec<Sample>) -> ()) {
  loop {
    let mut samples = Vec::new();
    for _ in 0..500 {
      let arr = parse_line(reader, 0, 0);
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

fn parse_line(reader: &mut BufReader<File>, skip: usize, limit: usize) -> Vec<String> {
  let mut buffer = Vec::new();

  let num_bytes = reader.read_until(b'\n', &mut buffer)
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
    .filter(|(i, _)| -> bool {
      if limit > 0 {
        return i < &limit;
      }
      return true;
    })
    .map(|(_, e)| String::from_utf8(e.as_bytes().to_vec()).unwrap_or(String::new()))
    .collect();
  
  return arr;
}
