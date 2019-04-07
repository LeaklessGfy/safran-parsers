extern crate actix_web;

mod api;
mod service;
mod parser;

use actix_web::server;

fn main() {
  server::new(|| api::create_app())
    .bind("127.0.0.1:8088")
    .unwrap()
    .run();
}
