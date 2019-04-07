use std::fs::File;
use std::str;
use std::io::{BufRead, BufReader};

pub mod samples_parser;
pub mod alarms_parser;

fn parse_line(reader: &mut BufReader<File>, skip: usize, limit: usize) -> Result<Vec<String>, String> {
  let mut buffer = Vec::new();

  let num_bytes = match reader.read_until(b'\n', &mut buffer) {
    Ok(num_bytes) => num_bytes,
    Err(_) => return Err("Error while reading".to_string()) 
  };
  
  if num_bytes < 1 {
    return Ok(Vec::new());
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
    .map(|mut s| {
      if s.ends_with('\n') {
        s.pop();
      }
      s
    })
    .collect();

  return Ok(arr);
}

fn read_empty_line(reader: &mut BufReader<File>) -> Result<(), String> {
  let mut e = String::new();
  match reader.read_line(&mut e) {
    Ok(_) => return Ok(()),
    Err(_) => return Err("Error while reading".to_string()),
  };
}
