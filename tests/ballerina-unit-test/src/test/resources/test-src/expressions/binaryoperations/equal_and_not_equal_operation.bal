// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

type OpenEmployee record {
    string|json name;
    int id;
};

type OpenPerson record {
    string name;
};

type ClosedEmployee record {
    string name;
    int id;
    !...
};

type OpenEmployeeTwo record {
    string name;
    int...
};

type OpenRecordWithOptionalFieldOne record {
    string name;
    int one?;
};

type OpenRecordWithOptionalFieldTwo record {
    string name;
    int two?;
};

type ClosedRecordWithOptionalFieldOne record {
    string name;
    int one?;
    !...
};

type ClosedRecordWithOptionalFieldTwo record {
    string name;
    int two?;
    !...
};

function checkBooleanEqualityPositive(boolean a, boolean b) returns boolean {
    return (a == b) && !(a != b);
}

function checkBooleanEqualityNegative(boolean a, boolean b) returns boolean {
    return (a == b) || !(a != b);
}

function checkIntEqualityPositive(int a, int b) returns boolean {
    return (a == b) && !(a != b);
}

function checkIntEqualityNegative(int a, int b) returns boolean {
    return (a == b) || !(a != b);
}

function checkByteEqualityPositive(byte a, byte b) returns boolean {
    return (a == b) && !(a != b);
}

function checkByteEqualityNegative(byte a, byte b) returns boolean {
    return (a == b) || !(a != b);
}

function checkFloatEqualityPositive(float a, float b) returns boolean {
    return (a == b) && !(a != b);
}

function checkFloatEqualityNegative(float a, float b) returns boolean {
    return (a == b) || !(a != b);
}

function checkStringEqualityPositive(string a, string b) returns boolean {
    return (a == b) && !(a != b);
}

function checkStringEqualityNegative(string a, string b) returns boolean {
    return (a == b) || !(a != b);
}

function checkEqualityToNilPositive(any a) returns boolean {
    return (a == ()) && !(a != ());
}

function checkEqualityToNilNegative(any a) returns boolean {
    return (a == ()) || !(a != ());
}

function checkOpenRecordEqualityPositive() returns boolean {
    OpenEmployee e1 = { name: "Em", id: 4000 };
    OpenEmployee e2 = e1;

    OpenEmployee e3 = { name: "Em" };
    OpenPerson e4 = { name: "Em", id: 0 };

    OpenEmployee e5 = { name: "Em", id: 4000, dept: "finance" };
    OpenEmployee e6 = { name: "Em", id: 4000, dept: "finance" };

    OpenEmployee e7 = {};
    OpenEmployee e8 = {};

    return (e1 == e2) && !(e1 != e2) && isEqual(e3, e4) && (e5 == e6) && !(e5 != e6) && (e7 == e8) && !(e7 != e8);
}

function checkOpenRecordEqualityNegative() returns boolean {
    OpenEmployee e1 = { name: "Em", id: 4000 };
    OpenEmployee e2 = {};

    OpenEmployee e3 = { name: "Em", id: 4000 };
    OpenEmployee e4 = { name: "Em", area: 51 };

    OpenEmployee e5 = { name: "Em" };
    OpenPerson e6 = { name: "Em" };

    OpenEmployee e7 = { name: "Em", id: 4000, dept: "finance" };
    OpenEmployee e8 = { name: "Em", id: 4000, dept: "hr" };

    return (e1 == e2) || !(e1 != e2) || (e3 == e4) || !(e3 != e4) || isEqual(e5, e6) || (e7 == e8) || !(e7 != e8);
}

function testOpenRecordWithOptionalFieldsEqualityPositive() returns boolean {
    OpenRecordWithOptionalFieldOne e1 = { name: "Em", one: 4000, two: 3000 };
    OpenRecordWithOptionalFieldOne e2 = e1;

    OpenRecordWithOptionalFieldOne e3 = { name: "Em" };
    OpenRecordWithOptionalFieldTwo e4 = { name: "Em" };

    OpenRecordWithOptionalFieldOne e5 = { name: "Em", one: 4000, two: 3000 };
    OpenRecordWithOptionalFieldTwo e6 = { name: "Em", two: 3000, one: 4000 };

    return (e1 == e2) && !(e1 != e2) && isEqual(e3, e4) && (e5 == e6) && !(e5 != e6);
}

function testOpenRecordWithOptionalFieldsEqualityNegative() returns boolean {
    OpenRecordWithOptionalFieldOne e1 = { name: "Em" };
    OpenRecordWithOptionalFieldTwo e2 = { name: "Zee" };

    OpenRecordWithOptionalFieldOne e3 = { name: "Em", one: 4000 };
    OpenRecordWithOptionalFieldTwo e4 = { name: "Em", two: 4000 };

    return (e1 == e2) || !(e1 != e2) || (e3 == e4) || !(e3 != e4);
}

function testClosedRecordWithOptionalFieldsEqualityPositive() returns boolean {
    ClosedRecordWithOptionalFieldOne e1 = { name: "Em", one: 4000 };
    ClosedRecordWithOptionalFieldOne e2 = e1;

    ClosedRecordWithOptionalFieldOne e3 = { name: "Em" };
    ClosedRecordWithOptionalFieldTwo e4 = { name: "Em" };

    return (e1 == e2) && !(e1 != e2) && isEqual(e3, e4);
}

function testClosedRecordWithOptionalFieldsEqualityNegative() returns boolean {
    ClosedRecordWithOptionalFieldOne e1 = { name: "Em" };
    ClosedRecordWithOptionalFieldTwo e2 = { name: "Zee" };

    ClosedRecordWithOptionalFieldOne e3 = { name: "Em", one: 4000 };
    ClosedRecordWithOptionalFieldTwo e4 = { name: "Em", two: 4000 };

    return (e1 == e2) || !(e1 != e2) || (e3 == e4) || !(e3 != e4);
}

function checkClosedRecordEqualityPositive() returns boolean {
    ClosedEmployee e1 = { name: "Em", id: 4000 };
    ClosedEmployee e2 = { name: "Em", id: 4000 };

    ClosedEmployee e3 = { name: "Em" };
    ClosedEmployee e4 = { name: "Em" };

    ClosedEmployee e5 = {};
    ClosedEmployee e6 = {};

    return isEqual(e1, e2) && (e3 == e4) && !(e3 != e4) && (e5 == e6) && !(e5 != e6);
}

function checkClosedRecordEqualityNegative() returns boolean {
    ClosedEmployee e1 = { name: "Em", id: 4000 };
    ClosedEmployee e2 = {};

    ClosedEmployee e3 = { name: "Em", id: 4000 };
    ClosedEmployee e4 = { name: "Em" };

    ClosedEmployee e5 = { name: "Em" };
    ClosedEmployee e6 = { name: "Em", id: 4100 };

    return (e1 == e2) || !(e1 != e2) || isEqual(e3, e4) || (e5 == e6) || !(e5 != e6);
}

function check1DArrayEqualityPositive(boolean[]|int[]|float[]|string[] a, boolean[]|int[]|float[]|string[] b)
             returns boolean {
    return (a == b) && !(a != b);
}

function check1DArrayEqualityNegative(boolean[]|int[]|float[]|string[] a, boolean[]|int[]|float[]|string[] b)
             returns boolean {
    return (a == b) || !(a != b);
}

function check1DClosedArrayEqualityPositive() returns boolean {
    boolean[4] b1;
    boolean[4] b2;

    boolean[3] b3 = [true, false, false];
    boolean[!...] b4 = [true, false, false];

    int[5] i1;
    int[5] i2;

    int[2] i3 = [123, 45678];
    int[2] i4 = [123, 45678];

    float[8] f1;
    float[8] f2;

    float[1] f3 = [12.3];
    float[!...] f4 = [12.3];

    byte[3] by1;
    byte[3] by2;

    byte[4] by3 = [0, 10, 100, 255];
    byte[!...] by4 = [0, 10, 100, 255];

    string[15] s1;
    string[15] s2;

    string[3] s3 = ["hello", "from", "ballerina"];
    string[3] s4 = ["hello", "from", "ballerina"];

    any[7] a1;
    any[7] a2;

    json j1 = { hello: "world", lang: "ballerina" };
    json j2 = { hello: "world",lang: "ballerina" };

    map m1 = { key1: "val1", key2: 2, key3: 3.1 };
    map m2 = { key1: "val1", key2: 2, key3: 3.1 };

    any[6] a3 = ["hi", 1, true, 54.3, j1, m1];
    any[6] a4 = ["hi", 1, true, 54.3, j2, m2];

    return (b1 == b2) && !(b1 != b2) && (b3 == b4) && !(b3 != b4) &&
        (i1 == i2) && !(i1 != i2) && (i3 == i4) && !(i3 != i4) &&
        (by1 == by2) && !(by1 != by2) && (by3 == by4) && !(by3 != by4) &&
        isEqual(f1, f2) && (f3 == f4) && !(f3 != f4) &&
        (s1 == s2) && !(s1 != s2) && isEqual(s3, s4) &&
        (a1 == a2) && !(a1 != a2) && (a3 == a4) && !(a3 != a4);
}

function check1DClosedArrayEqualityNegative() returns boolean {
    boolean[3] b1 = [true, false, false];
    boolean[!...] b2 = [true, false, true];

    int[2] i1 = [123, 45678];
    int[2] i2 = [123, 45674];

    byte[4] by1 = [123, 145, 255, 0];
    byte[4] by2 = [123, 45, 255, 0];

    float[1] f1 = [12.3];
    float[!...] f2 = [12.2];

    string[3] s1 = ["hello", "from", "ballerina"];
    string[3] s2 = ["hello", "from", "ball"];

    json j1 = { hello: "world", lang: "ball" };
    json j2 = { hello: "world",lang: "ballerina" };

    map m1 = { key1: "val1", key2: 2, key3: 3.1 };
    map m2 = { key1: "val1", key2: 2, key3: 3.1 };

    any[6] a1 = ["hi", 1, true, 54.3, j1, m1];
    any[6] a2 = ["hi", 1, true, 54.3, j2, m2];

    return (b1 == b2) || !(b1 != b2) || (i1 == i2) || !(i1 != i2) || (by1 == by2) || !(by1 != by2) ||
        (f1 == f2) || !(f1 != f2) || isEqual(s1, s2) || (a1 == a2) || !(a1 != a2);
}

function check1DAnyArrayEqualityPositive() returns boolean {
    json j1 = { hello: "world", lang: "ballerina" };
    json j2 = { hello: "world",lang: "ballerina" };

    map m1 = { key1: "val1", key2: 2, key3: 3.1 };
    map m2 = { key1: "val1", key2: 2, key3: 3.1 };

    any[] a = ["hi", 1, j1, 2.3, false, m1];
    any[] b = ["hi", 1, j2, 2.3, false, m2];

    return (a == b) && !(a != b);
}

function check1DAnyArrayEqualityNegative() returns boolean {
    json j1 = { hello: "world", lang: "ballerina" };
    json j2 = { hello: "world",lang: "ballerina" };

    map m1 = { key1: "val1", key2: 2, key3: 3.1 };
    map m2 = { key1: "val1", key2: 2, key3: 3.1 };

    any[] a = ["hi", 1, j1, 2.1, false, m1];
    any[] b = ["hi", 1, j2, 2.3, false, m2];

    return (a == b) || !(a != b);
}

function checkOpenClosedArrayEqualityPositive() returns boolean {
    string[!...] a = ["a", "bcd", "ef"];
    string[] b = ["a", "bcd", "ef"];

    (int|float)[] c = [5, 4.12, 54, 23.1];
    (float|int)[4] d = [5, 4.12, 54, 23.1];

    return (a == b) && !(a != b) && (c == d) && !(c != d);
}

function checkOpenClosedArrayEqualityNegative() returns boolean {
    boolean[] a = [true, false];
    boolean[3] b = [true, false, false];

    string[] c = ["true", "false", "false", "true"];
    string[4] d = ["true", "false", "false", "false"];

    return (a == b) || !(a != b) || (c == d) || !(c != d);
}

function check2DBooleanArrayEqualityPositive() returns boolean {
    boolean[][] b1 = [[], [true, false, false], [false]];
    boolean[][] b2 = [[], [true, false, false], [false]];

    return b1 == b2 && !(b1 != b2);
}

function check2DBooleanArrayEqualityNegative() returns boolean {
    boolean[][] b1 = [[], [true, false, false], [false]];
    boolean[][] b2 = [[], [false, false, false], [false]];

    boolean[][] b3 = [[], [true, false, false], [false]];
    boolean[][] b4 = [[], [true, false, false]];

    return b1 == b2 || !(b1 != b2) || b3 == b4 || !(b3 != b4);
}

function check2DIntArrayEqualityPositive() returns boolean {
    int[][] i1 = [[1], [], [100, 1200000, 9475883]];
    int[][] i2 = [[1], [], [100, 1200000, 9475883]];

    return i1 == i2 && !(i1 != i2);
}

function check2DIntArrayEqualityNegative() returns boolean {
    int[][] i1 = [[], [100, 2222, 111102], [294750]];
    int[][] i2 = [[], [100, 2222, 2349586], [294750]];

    int[][] i3 = [[1], [], [100, 1200000, 9475883]];
    int[][] i4 = [[1], []];

    return i1 == i2 || !(i1 != i2) || i3 == i4 || !(i3 != i4);
}

function check2DByteArrayEqualityPositive() returns boolean {
    byte[][] b1 = [[1, 100], [0, 255, 45], []];
    byte[][] b2 = [[1, 100], [0, 255, 45], []];

    return b1 == b2 && !(b1 != b2);
}

function check2DByteArrayEqualityNegative() returns boolean {
    byte[][] b1 = [[1, 100], [0, 255, 145], []];
    byte[][] b2 = [[1, 100], [0, 255, 45], []];

    byte[][] b3 = [[1, 100], [0, 255, 45], [23, 234]];
    byte[][] b4 = [[1, 100], [0, 255, 45]];

    return b1 == b2 || !(b1 != b2) || b3 == b4 || !(b3 != b4);
}

function check2DFloatArrayEqualityPositive() returns boolean {
    float[][] f1 = [[1.221], [], [10.0, 123.45, 9.475883]];
    float[][] f2 = [[1.221], [], [10.0, 123.45, 9.475883]];

    return f1 == f2 && !(f1 != f2);
}

function check2DFloatArrayEqualityNegative() returns boolean {
    float[][] f1 = [[1.221], [], [10.0, 123.45, 9.475883]];
    float[][] f2 = [[1.221], [], [10.0, 1.2, 9.475883]];

    float[][] f3 = [[1.221], [], [10.0, 123.45, 9.475883]];
    float[][] f4 = [[1.221], []];

    return f1 == f2 || !(f1 != f2) || f3 == f4 || !(f3 != f4);
}

function check2DStringArrayEqualityPositive() returns boolean {
    string[][] s1 = [[], ["hello world", "from", "ballerina"], ["ballet shoes"]];
    string[][] s2 = [[], ["hello world", "from", "ballerina"], ["ballet shoes"]];

    return s1 == s2 && !(s1 != s2);
}

function check2DStringArrayEqualityNegative() returns boolean {
    string[][] s1 = [[], ["hello", "from", "ballerina"], ["ballet shoes"]];
    string[][] s2 = [[], ["hi", "from", "ballerina"], ["ballet shoes"]];

    string[][] s3 = [[], ["hello", "from", "ballerina"], ["ballet shoes"]];
    string[][] s4 = [[], ["hello", "from", "ballerina"]];

    return s1 == s2 || !(s1 != s2) || s3 == s4 || !(s3 != s4);
}

function checkComplex2DArrayEqualityPositive() returns boolean {
    (int, float)[][] a;
    (int|string, float)[][] b;

    boolean equals = a == b && !(a != b);

    a = [[(1, 3.0)], [(123, 65.4), (234, 23.22)]];
    b[0] = [(1, 3.0)];
    b[1] = [(123, 65.4), (234, 23.22)];

    return equals && a == b && !(a != b);
}

function checkComplex2DArrayEqualityNegative() returns boolean {
    (int, float)[][] a = [[(123, 65.4), (234, 23.22)]];
    (int|string, float)[][] b = [[(124, 65.4), (234, 23.22)]];

    boolean equals = a == b || !(a != b);

    b = [[(123, 65.4), (234, 23.22)]];
    b[2] = [(123, 65.4), (234, 23.22)];

    return equals || a == b || !(a != b);
}

function checkMapEqualityPositive() returns boolean {
    map m1;
    map m2;

    map<string> m3;
    map<string> m4;

    map<float> m5;
    map<float> m6;

    boolean equals = m1 == m2 && !(m1 != m2) && m3 == m4 && !(m3 != m4);

    m1["one"] = 1;
    m2.one = 1;
    m2["two"] = "two";
    m1["two"] = "two";
    m1["three"] = 3.0;
    m2["three"] = 3.0;

    m3.last = "last";
    m3["a"] = "a";
    m4.a = "a";
    m4["last"] = "last";

    m5["one"] = 1.0;
    m6.one = 1.0;

    equals = equals && m1 == m2 && !(m1 != m2) && m3 == m4 && !(m3 != m4);
    return equals;
}

function checkMapEqualityNegative() returns boolean {
    map m1;
    map m2;

    m1.one = "hi";
    m2.one = "hello";

    map<int> m3;
    map<int> m4;

    m3.one = 1;
    m4.two = 2;

    return m1 == m2 || !(m1 != m2) || m3 == m4 || !(m3 != m4);
}

function checkComplexMapEqualityPositive() returns boolean {
    map<map<(boolean, string|float)>> m1;
    map<map<(boolean, float)|int>> m2;

    boolean equals = m1 == m2 && !(m1 != m2);

    map<(boolean, string|float)> m3 = { one: (true, 3.8), two: (false, 13.8) };
    m1["one"] = m3;

    map<(boolean, float)|int> m4;
    m4.one = (true, 3.8);
    m4.two = (false, 13.8);
    m2.one = m4;

    return equals && m1 == m2 && !(m1 != m2);
}

function checkComplexMapEqualityNegative() returns boolean {
    map<map<(boolean, string|float)>> m1;
    map<map<(boolean, float)|int>> m2;

    map<(boolean, string|float)> m3 = { one: (true, "3.8"), two: (false, 13.8) };
    m1.x = m3;

    map<(boolean, float)|int> m4 = { one: (true, 3.8), two: (false, 13.8) };
    m2.x = m4;

    return m1 == m2 || !(m1 != m2);
}

function checkTupleEqualityPositive() returns boolean {
    (string, int) t1 = ("", 0);
    (string, int) t2 = ("", 0);

    (string, int, OpenEmployee) t3 = ("hi", 0, { name: "Em" });
    (string, int, OpenEmployee) t4 = ("hi", 0, { name: "Em" });

    return t1 == t2 && !(t1 != t2) && t3 == t4 && !(t3 != t4);
}

function checkTupleEqualityNegative() returns boolean {
    (boolean, int) t1 = (false, 0);
    (boolean, int) t2 = (true, 0);

    (float, int, float) t3 = (1.0, 0, 1.1);
    (float, int, float) t4 = (1.1, 0, 1.0);

    (string, ClosedEmployee) t5 = ("hi", { name: "EmZee" });
    (string, ClosedEmployee) t6 = ("hi", { name: "Em" });

    return t1 == t2 || !(t1 != t2) || t3 == t4 || !(t3 != t4) || t5 == t6 || !(t5 != t6);
}

function checkUnionConstrainedMapsPositive() returns boolean {
    map<string|int> m1;
    map<int> m2;

    boolean equals = m1 == m2 && !(m1 != m2);

    m1["one"] = 1;
    m1["two"] = 2;

    m2["one"] = 1;
    m2["two"] = 2;

    equals = equals && m1 == m2 && !(m1 != m2);

    map<string|int> m3;
    map<int|float|string> m4;

    equals = equals && m3 == m4 && !(m3 != m4);

    m3["one"] = "one";
    m3["two"] = 2;

    m4["two"] = 2;
    m4["one"] = "one";

    equals = equals && m3 == m4 && !(m3 != m4);

    map<(string|int, float)>|(string,int) m5;
    map<(string, float)> m6;

    map<(string|int, float)> m7 = {
        one: ("hi", 100.0),
        two: ("hello", 21.1)
    };
    m5 = m7;
    m6.two = ("hello", 21.1);
    m6.one = ("hi", 100.0);

    return equals && m5 == m6 && !(m5 != m6);
}

function checkUnionConstrainedMapsNegative() returns boolean {
    map<int|boolean> m1;
    map<int> m2;

    m1["one"] = true;
    m1["two"] = 2;

    boolean equals = m1 == m2 || !(m1 != m2);

    m2["two"] = 2;

    equals = equals || m1 == m2 || !(m1 != m2);

    map<(string|int, float)>|(string,int) m3;
    map<(string, float)> m4;

    map<(string|int, float)> m5 = {
        one: ("hi", 100),
        two: ("hello", 21)
    };
    m3 = m5;
    m4.two = ("hello", 21.1);
    m4.one = ("hi", 100.0);

    return equals || m3 == m4 || !(m3 != m4);
}

function checkUnionArrayPositive() returns boolean {
    (string|int)[] a1;
    int[] a2;

    boolean equals = a1 == a2 && !(a1 != a2);

    a1 = [1, 2000, 937];
    a2 = [1, 2000, 937];

    equals = equals && a1 == a2 && !(a1 != a2);

    (string|int)[] a3;
    (int|float|string)[] a4;

    equals = equals && a3 == a4 && !(a3 != a4);

    a3[0] = "one";
    a3[3] = 3;

    a4[3] = 3;
    a4[0] = "one";

    equals = equals && a3 == a4 && !(a3 != a4);

    ((string, int)|float)[] a5;
    ((string, int|boolean)|float)[] a6;

    equals = equals && a5 == a6 && !(a5 != a6);

    a5 = [("hi", 1), 3.0];
    a6[0] = ("hi", 1);
    a6[1] = 3.0;

    return equals && a5 == a6 && !(a5 != a6);
}

function checkUnionArrayNegative() returns boolean {
    (string|int)[] a1;
    int[] a2;

    a1[0] = "true";
    a1[2] = 2;

    boolean equals = a1 == a2 || !(a1 != a2);

    a2[2] = 2;

    equals = equals || a1 == a2 || !(a1 != a2);

    ((string, int)|float)[] a5 = [("hi", 1), 3.0];
    ((string, int|boolean)|float)[] a6 = [("hi", true), 3.0];

    return equals || a5 == a6 || !(a5 != a6);
}

function checkTupleWithUnionPositive() returns boolean {
    (int, string|int, boolean) t1 = (1, "ballerina", false);
    (int, string|int, boolean) t2 = (1, "ballerina", false);

    boolean equals = t1 == t2 && !(t1 != t2);

    (int, string|int, boolean) t3 = (1000, "ballerina", true);
    (int, int|string, OpenEmployee|boolean|int) t4 = (1000, "ballerina", true);

    return equals && t3 == t4 && !(t3 != t4);
}

function checkTupleWithUnionNegative() returns boolean {
    (int, string|int, boolean) t1 = (1, "hi", false);
    (int, string|int, boolean) t2 = (1, "ballerina", false);

    boolean equals = t1 == t2 || !(t1 != t2);

    (int, string|int, boolean) t3 = (1000, "ballerina", true);
    (int, int|string, OpenEmployee|boolean|string) t4 = (1000, "ballerina", "true");

    return equals || t3 == t4 || !(t3 != t4);
}

function checkJsonEqualityPositive(json a, json b) returns boolean {
    return (a == b) && !(a != b);
}

function checkJsonEqualityNegative(json a, json b) returns boolean {
    return (a == b) || !(a != b);
}

function testIntByteEqualityPositive() returns boolean {
    int a;
    byte b;

    boolean equals = a == b && !(a != b);

    a = 5;
    b = 5;

    equals = equals && (a == b) && !(a != b);

    int[] c;
    byte[] d;

    equals = equals && (c == d) && !(c != d);

    c = [1, 2];
    d = [1, 2];

    equals = equals && (c == d) && !(c != d);

    byte[][] e = [[23, 45], [123, 43, 68]];
    (int|float)[][] f = [[23, 45], [123, 43, 68]];

    equals = equals && (e == f) && !(e != f);

    map<(byte, boolean)> g;
    map<(int, boolean)> h;

    equals = equals && (g == h) && !(g != h);

    g.one = (100, true);
    h.two = (1, false);
    h.one = (100, true);
    g.two = (1, false);

    return equals && (g == h) && !(g != h);
}

function testIntByteEqualityNegative() returns boolean {
    int a = 15;
    byte b = 5;

    boolean equals = a == b || !(a != b);

    a = 256;
    b = 0;

    equals = equals || a == b || !(a != b);

    int[] c = [2, 1];
    byte[] d = [1, 2];

    equals = equals || (c == d) || !(c != d);

    (int|float)[][] e = [[2.3, 45], [124, 43, 68]];
    byte[][] f = [[23, 45], [123, 43, 68]];

    equals = equals || (e == f) || !(e != f);

    map<(int, boolean)> g;
    map<(byte, boolean)> h;

    g.one = (10, true);
    h.two = (1, false);
    h.one = (100, true);
    g.two = (1, false);

    return equals && (g == h) && !(g != h);
}

function testPrimitiveAndJsonEqualityPositive() returns boolean {
    json a;
    () b;

    boolean equals = a == b && !(a != b);

    a = 1;
    int c = 1;

    equals = equals && (a == c) && !(a != c);

    a = "Hello World, from Ballerina";
    string d = "Hello World, from Ballerina";

    equals = equals && (a == d) && !(a != d);

    a = false;
    boolean|int e = false;

    equals = equals && (a == e) && !(a != e);

    json[] f = [1.5, 4.23, 2.1];
    (map|float)[] g = [1.5, 4.23, 2.1];

    equals = equals && (f == g) && !(f != g);

    OpenEmployee? h = ();
    a = ();

    return equals && (a == h) && !(a != h);
}

function testPrimitiveAndJsonEqualityNegative() returns boolean {
    json a;
    int? b = 5;

    boolean equals = a == b || !(a != b);

    a = 10;
    int c = 1;

    equals = equals || (a == c) || !(a != c);

    a = "Hello from Ballerina";
    string d = "Hello World, from Ballerina";

    equals = equals || (a == d) || !(a != d);

    a = false;
    boolean|int e = true;

    equals = equals || (a == e) || !(a != e);

    json[] f = [1.5, 4.23, 2.1];
    (map|int|float)[] g = [1, 4];

    equals = equals || (f == g) || !(f != g);

    OpenEmployee? h = ();
    a = 10;

    return equals || (a == h) || !(a != h);
}

function testSimpleXmlPositive() returns boolean {
    xml x1 = xml `<book>The Lost World</book>`;
    xml x2 = xml `<book>The Lost World</book>`;
    xml x3 = xml `Hello, world!`;
    xml x4 = xml `Hello, world!`;
    xml x5 = xml `<?target data?>`;
    xml x6 = xml `<?target data?>`;
    xml x7 = xml `<!-- I'm a comment -->`;
    xml x8 = xml `<!-- I'm a comment -->`;
    return x1 == x2 && !(x1 != x2) && x3 == x4 && !(x3 != x4) && x5 == x6 && !(x5 != x6) && x7 == x8 && !(x7 != x8);
}

function testSimpleXmlNegative() returns boolean {
    xml x1 = xml `<book>The Lot World</book>`;
    xml x2 = xml `<book>The Lost World</book>`;
    xml x3 = xml `Hello, world!`;
    xml x4 = xml `Hello world!`;
    xml x5 = xml `<?targt data?>`;
    xml x6 = xml `<?target data?>`;
    xml x7 = xml `<!-- I'm comment one -->`;
    xml x8 = xml `<!-- I'm comment two -->`;
    return x1 == x2 || !(x1 != x2) || x3 == x4 || !(x3 != x4) || x5 == x6 || !(x5 != x6) || x7 == x8 || !(x7 != x8);
}

public function testEqualNestedXml() returns boolean {
    xml x1 = xml `<book><name>The Lost World<year>1912</year></name></book>`;
    xml x2 = xml `<book><name>The Lost World<year>1912</year></name></book>`;
    return x1 == x2 && !(x1 != x2);
}

public function testUnequalNestedXml() returns boolean {
    xml x1 = xml `<book><name>The Lost World<year>1920</year></name></book>`;
    xml x2 = xml `<book><name>The Lost World<year>1912</year></name></book>`;
    return x1 == x2 || !(x1 != x2);
}

public function testEqualXmlWithComments() returns boolean {
    xml x1 = xml `<book><name>The Lost World<!-- I'm a comment --></name></book>`;
    xml x2 = xml `<book><name>The Lost World<!-- I'm a comment --></name></book>`;
    return x1 == x2 && !(x1 != x2);
}

public function testUnequalXmlWithUnequalComment() returns boolean {
    xml x1 = xml `<book><name>The Lost World<!-- Don't Ignore me --></name></book>`;
    xml x2 = xml `<book><name>The Lost World<!-- Ignore me --></name></book>`;
    return x1 == x2 || !(x1 != x2);
}

public function testEqualXmlIgnoringAttributeOrder() returns boolean {
    xml x1 = xml `<book><name category="fiction" year="1912">The Lost World<author>Doyle</author></name></book>`;
    xml x2 = xml `<book><name year="1912" category="fiction">The Lost World<author>Doyle</author></name></book>`;
    return x1 == x2 && !(x1 != x2);
}

public function testUnequalXmlIgnoringAttributeOrder() returns boolean {
    xml x1 = xml `<book><name category="fantasy" year="1912">The Lost World<author>Doyle</author></name></book>`;
    xml x2 = xml `<book><name year="1912" category="fiction">The Lost World<author>Doyle</author></name></book>`;
    return x1 == x2 || !(x1 != x2);
}

public function testEqualXmlWithPI() returns boolean {
    xml x1 = xml `<book><?target data?><name>The Lost World</name><?target_two data_two?></book>`;
    xml x2 = xml `<book><?target data?><name>The Lost World</name><?target_two data_two?></book>`;
    return x1 == x2 && !(x1 != x2);
}

public function testUnequalXmlWithUnequalPI() returns boolean {
    xml x1 = xml `<book><?target data?><name>The Lost World</name></book>`;
    xml x2 = xml `<book><?target dat?><name>The Lost World</name></book>`;
    return x1 == x2 || !(x1 != x2);
}

public function testUnequalXmlWithPIInWrongOrder() returns boolean {
    xml x1 = xml `<book><?target data?><name>The Lost World</name></book>`;
    xml x2 = xml `<book><name>The Lost World</name><?target data?></book>`;
    return x1 == x2 || !(x1 != x2);
}

public function testUnequalXmlWithMultiplePIInWrongOrder() returns boolean {
    xml x1 = xml `<book><?target data?><?target_two data_two?><name>The Lost World</name></book>`;
    xml x2 = xml `<book><?target_two data_two?><?target data?><name>The Lost World</name></book>`;
    return x1 == x2 || !(x1 != x2);
}

public function testUnequalXmlWithMissingPI() returns boolean {
    xml x1 = xml `<book><?target data?><name>The Lost World</name></book>`;
    xml x2 = xml `<book><name>The Lost World</name></book>`;
    return x1 == x2 || !(x1 != x2);
}

public function testXmlWithNamespacesPositive() returns boolean {
    xml x1 = xml `<book xmlns="http://wso2.com"><name>The Lost World<year>1912</year></name></book>`;
    xml x2 = xml `<book xmlns="http://wso2.com"><name>The Lost World<year>1912</year></name></book>`;

    xmlns "http://wso2.com";
    xml x3 = xml `<book><name>The Lost World<year>1912</year></name></book>`;

    return x1 == x2 && !(x1 != x2) && x2 == x3 && !(x2 != x3);
}

public function testXmlWithNamespacesNegative() returns boolean {
    xml x1 = xml `<book xmlns="http://wso2.com"><name>The Lost World<year>1912</year></name></book>`;
    xml x2 = xml `<book xmlns="http://wsotwo.com"><name>The Lost World<year>1912</year></name></book>`;
    xml x3 = xml `<book><name>The Lost World<year>1912</year></name></book>`;

    return x1 == x2 || !(x1 != x2) || x2 == x3 && !(x2 != x3);
}

public function testXmlSequenceAndXmlItemEqualityPositive() returns boolean {
    xml x1 = xml `<name>Book One</name>`;
    xml x2 = x1.select("name");
    return x1 == x2 && !(x1 != x2) && x2 == x1 && !(x2 != x1);
}

public function testXmlSequenceAndXmlItemEqualityNegative() returns boolean {
    xml x1 = xml `<name>Book One</name>`;
    xml x2 = xml `<name>Book Two</name>`;
    xml x3 = x2.select("name");
    return x1 == x3 || !(x1 != x3) || x3 == x1 || !(x3 != x1);
}

public function testJsonRecordMapEqualityPositive() returns boolean {
    OpenEmployeeTwo e = { name: "Maryam", id: 1000 };

    json<ClosedEmployee> j = { name: "Maryam", id: 1000 };
    json j2 = j;

    map<string|int> m = { name: "Maryam", id: 1000 };
    map m2 = m;

    return e == m && !(m != e) && e == m2 && !(m2 != e) && e == j && !(j != e) && e == j2 && !(j2 != e);
}

public function testJsonRecordMapEqualityNegative() returns boolean {
    OpenEmployeeTwo e = { name: "Zee", id: 1000 };

    json<ClosedEmployee> j = { name: "Maryam", id: 122 };
    json j2 = j;

    map<string|int> m = { name: "Maryam" };
    map m2 = m;

    return e == m || !(m != e) || e == m2 || !(m2 != e) || e == j || !(j != e) || e == j2 || !(j2 != e);
}

function isEqual(any a, any b) returns boolean {
    return a == b && !(a != b);
}
