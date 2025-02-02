
uniffi::setup_scaffolding!();

#[uniffi::export(default(is_jna_lib = true))]
pub fn lib_main(is_jna_lib: bool) {
    println!("Hello, from native library!");

    use rustix::process::*;
    use rustix::thread::*;

    let pid = Pid::as_raw(Some(getpid()));
    let tid = Pid::as_raw(Some(gettid()));
    let is_main_thread = tid == pid;

    let offset = tid - pid;
    let offset_expected = if is_jna_lib {
        4
    } else {
        0
    };

    println!("ProcessId\t\t\t: {}", pid);
    println!("ThreadId\t\t\t: {}", tid);
    println!("Running on main thread\t: {}", is_main_thread); // or offset = 0
    println!("Running from jvm\t\t: {}", is_jna_lib);

    if is_main_thread == false {
        println!("The offset of pid and tid is\t: {}", offset);

        /*
        println!("IMHO the offset with recent linux and openjdk is {}: {}",
            offset_expected,
            tid - pid == offset_expected
        );
        */


        let thread_ids = list_threads(pid as u32).unwrap();

        println!("jna-thread is consecutive to main thread\t: {}", thread_ids.get(1) == Some(&(tid as u32)));

        println!("ThreadIds of process: {}", pid);
        println!("{:?}", thread_ids.clone());
        /*
            for id in thread_ids.clone().iter() {
                println!("{:?}", id);
            }
        */

        let mut counter = 0;
        let seconds = 5;
        println!();
        println!("Waiting {} seconds before leaving native world. This should run blocking.", seconds);
        for i in 0 .. seconds {
            println!("{}", i +1);
            counter += 1;

            use std::{thread, time};
            thread::sleep(time::Duration::from_secs(1));
        }
    }
}

use std::fs;
use std::io;

fn list_threads(pid: u32) -> io::Result<Vec<u32>> {
    let mut thread_ids = Vec::new();
    // Pfad zum task-Verzeichnis des Prozesses
    let task_path = format!("/proc/{}/task", pid);

    // Lese das task-Verzeichnis aus
    for entry in fs::read_dir(task_path)? {
        let entry = entry?;
        // Versuche den Namen des Verzeichnisses in einen String zu konvertieren
        if let Ok(name) = entry.file_name().into_string() {
            // Parse den Namen als u32, falls möglich
            if let Ok(tid) = name.parse::<u32>() {
                thread_ids.push(tid);
            }
        }
    }
    Ok(thread_ids)
}