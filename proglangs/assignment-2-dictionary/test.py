import subprocess

def run(input_text):
    process = subprocess.run(['./main'],input = input_text,capture_output = True, text = True)
    return (process.stdout + process.stderr).strip()

def test(input, expected):
    output = run(input)
    if output == expected:
        print(f'Test passed! Expected {expected}, got {output}')
    else:
        print(f'Test didn\'t pass! Expected {expected}, got {output}')

test("first","first")
test("second", "second")
test("third","third")
test("big","big")
test("A"*257,"There is no such key or an error occured while reading")
test("dj_arbuz","There is no such key or an error occured while reading")
test("","There is no such key or an error occured while reading")
