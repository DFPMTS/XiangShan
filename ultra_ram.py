import os

def insert_string_at_line(file_path, line_number, string_to_insert):
    with open(file_path, 'r') as file:
        lines = file.readlines()

    # Check if the file has at least `line_number` lines
    if len(lines) >= line_number:
        # Insert the string at the beginning of the specified line
        lines[line_number - 1] = string_to_insert + ' ' + lines[line_number - 1]

    with open(file_path, 'w') as file:
        file.writelines(lines)

def process_specific_files(directory, filenames, line_number, string_to_insert):
    for filename in filenames:
        file_path = os.path.join(directory, filename)
        if os.path.isfile(file_path):
            insert_string_at_line(file_path, line_number, string_to_insert)
            print(f"Processed: {file_path}")
        else:
            print(f"File not found: {file_path}")

if __name__ == "__main__":
    # Directory where the files are located
    directory = './build/rtl'

    # List of filenames to edit (only filenames, not full paths)
    filenames_to_edit = [
      "array_1_ext.v",
      "array_9_ext.v",
      "array_10_ext.v",
      "array_13_ext.v",
      "array_mcp2_0_ext.v",
      "array_0_3_ext.v",
      "array_17_ext.v",
      "array_18_ext.v",
      "array_19_ext.v",
    ]

    line_number = 14
    string_to_insert = '(* ram_style="ultra" *)'

    process_specific_files(directory, filenames_to_edit, line_number, string_to_insert)