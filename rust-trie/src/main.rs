use std::env;
use std::io::{Error, ErrorKind};

type Pointer = i32;
type Count = i32;
type Char = u8;

#[derive(Copy, Clone)]
struct Node {
    link: Pointer,
    count: Count,
}

fn word_for(parents: &[Pointer], mut p: Pointer) -> String {
    let mut drow = Vec::with_capacity(25); // sane max length
    while p != -1 {
        let c = p % 26;
        p = parents[(p / 26) as usize];
        drow.push(b'a' + (c as Char));
    }
    drow.reverse();
    String::from_utf8(drow).unwrap()
}

fn create_child(
    parents: &mut Vec<Pointer>,
    children: &mut Vec<Node>,
    p: Pointer,
    c: Char,
) -> Pointer {
    let h = children.len();
    children[p as usize].link = h as Pointer;
    children.resize(children.len() + 26, Node { link: -1, count: 0 });
    parents.push(p);
    (h as u32 + c as u32) as Pointer
}

fn find_child(
    parents: &mut Vec<Pointer>,
    children: &mut Vec<Node>,
    p: Pointer,
    c: Char,
) -> Pointer {
    let elem = children[p as usize];
    if elem.link == -1 {
        create_child(parents, children, p, c)
    } else {
        (elem.link as u32 + c as u32) as Pointer
    }
}

fn error(msg: &str) -> Error {
    Error::new(ErrorKind::Other, msg)
}

fn main() -> std::io::Result<()> {
    let args = env::args().collect::<Vec<String>>();
    if args.len() != 3 {
        return Err(error(&format!(
            "Usage: {} file-path limit-num",
            env::args().next().unwrap()
        )));
    }
    let mut data: Vec<u8> = std::fs::read(&args[1])?;
    for d in &mut data {
        *d = (*d | 32) - b'a';
    }

    let mut children: Vec<Node> = Vec::with_capacity(data.len() / 600);
    let mut parents: Vec<Pointer> = Vec::with_capacity(data.len() / 600 / 26);
    parents.push(-1);
    for _ in 0..26 {
        children.push(Node { link: -1, count: 0 });
    }

    let mut data = data.iter();

    while let Some(&c) = data.next() {
        if c < 26 {
            let mut p = c as Pointer;

            while let Some(&c) = data.next() {
                if c < 26 {
                    p = find_child(&mut parents, &mut children, p, c);
                } else {
                    break;
                }
            }
            children[p as usize].count += 1;
        }
    }

    let mut counts_words: Vec<(i32, String)> = Vec::new();
    for (i, e) in children.iter().enumerate() {
        if e.count != 0 {
            counts_words.push((-e.count, word_for(&parents, i as Pointer)));
        }
    }

    counts_words.sort();

    let limit = args[2]
        .parse()
        .map_err(|_| error("ARGV[2] must be in range: [1, usize_max]"))?;

    for (count, word) in counts_words.iter().take(limit) {
        println!("{word} {count}", word = word, count = -count);
    }

    Ok(())
}

