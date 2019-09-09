use std::io;

fn main() {
    println!("Hello, Echo!");

    loop {
        let mut message = String::new();
        io::stdin().read_line(&mut message).expect("Failed to read line");

        println!("Echo: {}", message)
    }
}