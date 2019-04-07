extern crate actix_web;
extern crate serde_json;

use actix_web::*;
use futures::{Stream, Future};

use crate::service::import;

pub fn create_app() -> App {
  App::new()
    .resource("/", |r| r.f(index_route))
    .resource("/import", |r| r.f(import_route))
}

fn index_route(_req: &HttpRequest) -> &'static str {
  "Hello world!"
}

fn import_route(req: &HttpRequest) -> &'static str {
  let m = req.multipart()
    .map(|item| {
      match item {
        multipart::MultipartItem::Field(field) => {
          println!("EE {:?}", field);
          ""
        },
        multipart::MultipartItem::Nested(mp) => {
          println!("EEE");
          ""
        },
      }
    })
    .collect();

  return "";
}
