use std::fs::File;
use std::str;
use std::io::{BufRead, BufReader};

pub mod samples_parser;
pub mod alarms_parser;

fn parse_line(reader: &mut BufReader<File>, skip: usize, limit: usize) -> Vec<String> {
  let mut buffer = Vec::new();

  let num_bytes = reader
    .read_until(b'\n', &mut buffer)
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
    .map(|(_, e)|
      String::from_utf8(
        e
          .as_bytes()
          .to_vec()
      ).unwrap_or(String::new())
    )
    .collect();

  return arr;
}

fn read_empty_line(reader: &mut BufReader<File>) {
  let mut e = String::new();
  reader.read_line(&mut e).expect("Error reading empty line");
}
