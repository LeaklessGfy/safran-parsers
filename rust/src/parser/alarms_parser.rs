use std::fs::File;
use std::io::BufReader;
use crate::parser::parse_line;

#[derive(Debug)]
pub struct Alarm {
  time: String,
  level: i64,
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

  pub fn parse_alarms(&mut self) -> Result<Vec<Alarm>, String> {
    let mut alarms = Vec::new();
    loop {
      let arr = parse_line(&mut self.reader, 0, 0)?;

      if arr.len() < 1 {
        return Ok(alarms);
      }

      if arr.len() < 3 {
        return Err("Bad alarms file format".to_string());
      }

      let time: Vec<String> = arr[0]
        .split(' ')
        .map(|s| String::from(s))
        .collect();

      if time.len() < 2 {
        return Err("Bad time format".to_string());
      }

      alarms.push(Alarm {
        time: time[1].clone(),
        level: arr[1].parse::<i64>().unwrap(),
        message: arr[2].clone(),
      });
    }
  }
}
