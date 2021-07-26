package com.uam.auxiliar;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Genera scripts Python para hacer derivación simbolica usando
 * la biblioteca SymPy (https://www.sympy.org/).
 * Véase https://www.sympygamma.com/ para una demostración interactiva.
 * Se usa Python3.
 * Para poder leer las expresiones en LaTex es necesario instalar (pip o conda) antlr4 <p>
 * $ pip3 install antlr4-python3-runtime <p>
 * o <p>
 * $ conda install --channel=conda-forge antlr-python-runtime <p>
 * <p>
 * Nota: Averiguar si las bibliotecas necesarias se pueden usar con jython y evitar la salida a shell
 *
 * @author Iván Gutiérrez
 */

public class SolucionaSimbolico {
    /**
     * Licencia BSD
     */
    public static final String LICENSE = "Copyright (c) 2008-2020 SymPy Development Team\n" +
            "\n" +
            "All rights reserved.\n" +
            "\n" +
            "Redistribution and use in source and binary forms, with or without\n" +
            "modification, are permitted provided that the following conditions are met:\n" +
            "\n" +
            "  a. Redistributions of source code must retain the above copyright notice,\n" +
            "     this list of conditions and the following disclaimer.\n" +
            "  b. Redistributions in binary form must reproduce the above copyright\n" +
            "     notice, this list of conditions and the following disclaimer in the\n" +
            "     documentation and/or other materials provided with the distribution.\n" +
            "  c. Neither the name of the SymPy nor the names of its contributors\n" +
            "     may be used to endorse or promote products derived from this software\n" +
            "     without specific prior written permission.\n" +
            "\n" +
            "\n" +
            "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"\n" +
            "AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE\n" +
            "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE\n" +
            "ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR\n" +
            "ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL\n" +
            "DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n" +
            "SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER\n" +
            "CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT\n" +
            "LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY\n" +
            "OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH\n" +
            "DAMAGE.";
    /**
     * Variable con scriptlet Python que deriva la expresión paso a paso <p>
     * Basado en: https://github.com/sympy/sympy_gamma/blob/master/app/logic/diffsteps.py <p></p>
     * Se tradujeron los mensajes al español. <p>
     * La función acomodaNotacion de Python se asegura de colocar paréntesis en la notación
     * de las derivadas, evitando ambigüedad. <p>
     * Se debería actualizar el código desde web y hacer automática la traducción
     *
     */
    private static final String DERIVADOR = "import sympy\n" +
            "import collections\n" +
            "\n" +
            "from sympy.core.function import AppliedUndef, Derivative\n" +
            "from sympy.functions.elementary.trigonometric import TrigonometricFunction\n" +
            "from sympy.strategies.core import switch\n" +
            "from sympy.core.compatibility import reduce\n" +
            "from sympy import factor\n" +
            "from sympy import simplify\n" +
            "from sympy import Symbol\n" +
            "from sympy import pi\n" +
            "from sympy import sin\n" +
            "from sympy import cos\n" +
            "from sympy import solve\n" +
            "from sympy.plotting import plot\n" +
            "\n" +
            "from contextlib import contextmanager\n" +
            "\n" +
            "from mpmath.libmp.backend import basestring\n" +
            "from sympy import latex, symbols\n" +
            "from sympy.parsing.latex import parse_latex\n" +
            "\n" +
            "\n" +
            "def Rule(name, props=\"\"):\n" +
            "    # GOTCHA: namedtuple class name not considered!\n" +
            "    def __eq__(self, other):\n" +
            "        return self.__class__ == other.__class__ and tuple.__eq__(self, other)\n" +
            "\n" +
            "    __neq__ = lambda self, other: not __eq__(self, other)\n" +
            "    cls = collections.namedtuple(name, props + \" context symbol\")\n" +
            "    cls.__eq__ = __eq__\n" +
            "    cls.__ne__ = __neq__\n" +
            "    return cls\n" +
            "\n" +
            "\n" +
            "def functionnames(numterms):\n" +
            "    if numterms == 2:\n" +
            "        return [\"f\", \"g\"]\n" +
            "    elif numterms == 3:\n" +
            "        return [\"f\", \"g\", \"h\"]\n" +
            "    else:\n" +
            "        return [\"f_{}\".format(i) for i in range(numterms)]\n" +
            "\n" +
            "\n" +
            "def replace_u_var(rule, old_u, new_u):\n" +
            "    d = rule._asdict()\n" +
            "    for field, val in d.items():\n" +
            "        if isinstance(val, sympy.Basic):\n" +
            "            d[field] = val.subs(old_u, new_u)\n" +
            "        elif isinstance(val, tuple):\n" +
            "            d[field] = replace_u_var(val, old_u, new_u)\n" +
            "        elif isinstance(val, list):\n" +
            "            result = []\n" +
            "            for item in val:\n" +
            "                if isinstance(item, tuple):\n" +
            "                    result.append(replace_u_var(item, old_u, new_u))\n" +
            "                else:\n" +
            "                    result.append(item)\n" +
            "            d[field] = result\n" +
            "    return rule.__class__(**d)\n" +
            "\n" +
            "\n" +
            "# def replace_all_u_vars(rule, replacements=None):\n" +
            "#     if replacements is None:\n" +
            "#         replacements = []\n" +
            "\n" +
            "#     d = rule._asdict()\n" +
            "#     for field, val in d.items():\n" +
            "#         if isinstance(val, sympy.Basic):\n" +
            "#             for dummy in val.find(sympy.Dummy):\n" +
            "#                 replacements.append((dummy, ))\n" +
            "#         elif isinstance(val, tuple):\n" +
            "#             pass\n" +
            "#     return rule.__class__(**d)\n" +
            "\n" +
            "class Printer(object):\n" +
            "    def __init__(self):\n" +
            "        self.lines = []\n" +
            "        self.level = 0\n" +
            "\n" +
            "    def append(self, text):\n" +
            "        self.lines.append(self.level * \"\\t\" + text)\n" +
            "\n" +
            "    def finalize(self):\n" +
            "        return \"\\n\".join(self.lines)\n" +
            "\n" +
            "    def format_math(self, math):\n" +
            "        return str(math)\n" +
            "\n" +
            "    def format_math_display(self, math):\n" +
            "        return self.format_math(math)\n" +
            "\n" +
            "    @contextmanager\n" +
            "    def new_level(self):\n" +
            "        self.level += 1\n" +
            "        yield self.level\n" +
            "        self.level -= 1\n" +
            "\n" +
            "    @contextmanager\n" +
            "    def new_step(self):\n" +
            "        yield self.level\n" +
            "        self.lines.append('\\n')\n" +
            "\n" +
            "\n" +
            "class LaTeXPrinter(Printer):\n" +
            "    def format_math(self, math):\n" +
            "        return latex(math)\n" +
            "\n" +
            "\n" +
            "class HTMLPrinterP(LaTeXPrinter):\n" +
            "    def __init__(self):\n" +
            "        super(HTMLPrinterP, self).__init__()\n" +
            "        self.lines = ['<ol>']\n" +
            "\n" +
            "    def format_math(self, math):\n" +
            "        return '<script type=\"math/tex; mode=inline\">{}</script>'.format(\n" +
            "            latex(math))\n" +
            "\n" +
            "    def format_math_display(self, math):\n" +
            "        if not isinstance(math, basestring):\n" +
            "            math = latex(math)\n" +
            "        return '<script type=\"math/tex; mode=display\">{}</script>'.format(\n" +
            "            math)\n" +
            "\n" +
            "    @contextmanager\n" +
            "    def new_level(self):\n" +
            "        self.level += 1\n" +
            "        self.lines.append(' ' * 4 * self.level + '<ol>')\n" +
            "        yield\n" +
            "        self.lines.append(' ' * 4 * self.level + '</ol><br/>')\n" +
            "        self.level -= 1\n" +
            "\n" +
            "    @contextmanager\n" +
            "    def new_step(self):\n" +
            "        self.lines.append(' ' * 4 * self.level + '<li>')\n" +
            "        yield self.level\n" +
            "        self.lines.append(' ' * 4 * self.level + '</li><br>')\n" +
            "\n" +
            "    @contextmanager\n" +
            "    def new_collapsible(self):\n" +
            "        self.lines.append(' ' * 4 * self.level + '<div class=\"collapsible\">')\n" +
            "        yield self.level\n" +
            "        self.lines.append(' ' * 4 * self.level + '</div>')\n" +
            "\n" +
            "    @contextmanager\n" +
            "    def new_u_vars(self):\n" +
            "        self.u, self.du = sympy.Symbol('u'), sympy.Symbol('du')\n" +
            "        yield self.u, self.du\n" +
            "\n" +
            "    def append(self, text):\n" +
            "        self.lines.append(' ' * 4 * (self.level + 1) + '<p>{}</p>'.format(text))\n" +
            "\n" +
            "    def append_header(self, text):\n" +
            "        self.lines.append(' ' * 4 * (self.level + 1) + '<h2>{}</h2>'.format(text))\n" +
            "\n" +
            "\n" +
            "########################################################3\n" +
            "\n" +
            "def Rule(name, props=\"\"):\n" +
            "    return collections.namedtuple(name, props + \" context symbol\")\n" +
            "\n" +
            "\n" +
            "ConstantRule = Rule(\"ConstantRule\", \"number\")\n" +
            "ConstantTimesRule = Rule(\"ConstantTimesRule\", \"constant other substep\")\n" +
            "PowerRule = Rule(\"PowerRule\", \"base exp\")\n" +
            "AddRule = Rule(\"AddRule\", \"substeps\")\n" +
            "MulRule = Rule(\"MulRule\", \"terms substeps\")\n" +
            "DivRule = Rule(\"DivRule\", \"numerator denominator numerstep denomstep\")\n" +
            "ChainRule = Rule(\"ChainRule\", \"substep inner u_var innerstep\")\n" +
            "TrigRule = Rule(\"TrigRule\", \"f\")\n" +
            "ExpRule = Rule(\"ExpRule\", \"f base\")\n" +
            "LogRule = Rule(\"LogRule\", \"arg base\")\n" +
            "FunctionRule = Rule(\"FunctionRule\")\n" +
            "AlternativeRule = Rule(\"AlternativeRule\", \"alternatives\")\n" +
            "DontKnowRule = Rule(\"DontKnowRule\")\n" +
            "RewriteRule = Rule(\"RewriteRule\", \"rewritten substep\")\n" +
            "\n" +
            "DerivativeInfo = collections.namedtuple('DerivativeInfo', 'expr symbol')\n" +
            "\n" +
            "evaluators = {}\n" +
            "\n" +
            "\n" +
            "def evaluates(rule):\n" +
            "    def _evaluates(func):\n" +
            "        func.rule = rule\n" +
            "        evaluators[rule] = func\n" +
            "        return func\n" +
            "\n" +
            "    return _evaluates\n" +
            "\n" +
            "\n" +
            "def power_rule(derivative):\n" +
            "    expr, symbol = derivative.expr, derivative.symbol\n" +
            "    base, exp = expr.as_base_exp()\n" +
            "\n" +
            "    if not base.has(symbol):\n" +
            "        if isinstance(exp, sympy.Symbol):\n" +
            "            return ExpRule(expr, base, expr, symbol)\n" +
            "        else:\n" +
            "            u = sympy.Dummy()\n" +
            "            f = base ** u\n" +
            "            return ChainRule(\n" +
            "                ExpRule(f, base, f, u),\n" +
            "                exp, u,\n" +
            "                diff_steps(exp, symbol),\n" +
            "                expr, symbol\n" +
            "            )\n" +
            "    elif not exp.has(symbol):\n" +
            "        if isinstance(base, sympy.Symbol):\n" +
            "            return PowerRule(base, exp, expr, symbol)\n" +
            "        else:\n" +
            "            u = sympy.Dummy()\n" +
            "            f = u ** exp\n" +
            "            return ChainRule(\n" +
            "                PowerRule(u, exp, f, u),\n" +
            "                base, u,\n" +
            "                diff_steps(base, symbol),\n" +
            "                expr, symbol\n" +
            "            )\n" +
            "    else:\n" +
            "        return DontKnowRule(expr, symbol)\n" +
            "\n" +
            "\n" +
            "def add_rule(derivative):\n" +
            "    expr, symbol = derivative.expr, derivative.symbol\n" +
            "    return AddRule([diff_steps(arg, symbol) for arg in expr.args],\n" +
            "                   expr, symbol)\n" +
            "\n" +
            "\n" +
            "def constant_rule(derivative):\n" +
            "    expr, symbol = derivative.expr, derivative.symbol\n" +
            "    return ConstantRule(expr, expr, symbol)\n" +
            "\n" +
            "\n" +
            "def mul_rule(derivative):\n" +
            "    expr, symbol = derivative\n" +
            "    terms = expr.args\n" +
            "    is_div = 1 / sympy.Wild(\"denominator\")\n" +
            "\n" +
            "    coeff, f = expr.as_independent(symbol)\n" +
            "\n" +
            "    if coeff != 1:\n" +
            "        return ConstantTimesRule(coeff, f, diff_steps(f, symbol), expr, symbol)\n" +
            "\n" +
            "    numerator, denominator = expr.as_numer_denom()\n" +
            "    if denominator != 1:\n" +
            "        return DivRule(numerator, denominator,\n" +
            "                       diff_steps(numerator, symbol),\n" +
            "                       diff_steps(denominator, symbol), expr, symbol)\n" +
            "\n" +
            "    return MulRule(terms, [diff_steps(g, symbol) for g in terms], expr, symbol)\n" +
            "\n" +
            "\n" +
            "def trig_rule(derivative):\n" +
            "    expr, symbol = derivative\n" +
            "    arg = expr.args[0]\n" +
            "\n" +
            "    default = TrigRule(expr, expr, symbol)\n" +
            "    if not isinstance(arg, sympy.Symbol):\n" +
            "        u = sympy.Dummy()\n" +
            "        default = ChainRule(\n" +
            "            TrigRule(expr.func(u), expr.func(u), u),\n" +
            "            arg, u, diff_steps(arg, symbol),\n" +
            "            expr, symbol)\n" +
            "\n" +
            "    if isinstance(expr, (sympy.sin, sympy.cos)):\n" +
            "        return default\n" +
            "    elif isinstance(expr, sympy.tan):\n" +
            "        f_r = sympy.sin(arg) / sympy.cos(arg)\n" +
            "\n" +
            "        return AlternativeRule([\n" +
            "            default,\n" +
            "            RewriteRule(f_r, diff_steps(f_r, symbol), expr, symbol)\n" +
            "        ], expr, symbol)\n" +
            "    elif isinstance(expr, sympy.csc):\n" +
            "        f_r = 1 / sympy.sin(arg)\n" +
            "\n" +
            "        return AlternativeRule([\n" +
            "            default,\n" +
            "            RewriteRule(f_r, diff_steps(f_r, symbol), expr, symbol)\n" +
            "        ], expr, symbol)\n" +
            "    elif isinstance(expr, sympy.sec):\n" +
            "        f_r = 1 / sympy.cos(arg)\n" +
            "\n" +
            "        return AlternativeRule([\n" +
            "            default,\n" +
            "            RewriteRule(f_r, diff_steps(f_r, symbol), expr, symbol)\n" +
            "        ], expr, symbol)\n" +
            "    elif isinstance(expr, sympy.cot):\n" +
            "        f_r_1 = 1 / sympy.tan(arg)\n" +
            "        f_r_2 = sympy.cos(arg) / sympy.sin(arg)\n" +
            "        return AlternativeRule([\n" +
            "            default,\n" +
            "            RewriteRule(f_r_1, diff_steps(f_r_1, symbol), expr, symbol),\n" +
            "            RewriteRule(f_r_2, diff_steps(f_r_2, symbol), expr, symbol)\n" +
            "        ], expr, symbol)\n" +
            "    else:\n" +
            "        return DontKnowRule(f, symbol)\n" +
            "\n" +
            "\n" +
            "def exp_rule(derivative):\n" +
            "    expr, symbol = derivative\n" +
            "    exp = expr.args[0]\n" +
            "    if isinstance(exp, sympy.Symbol):\n" +
            "        return ExpRule(expr, sympy.E, expr, symbol)\n" +
            "    else:\n" +
            "        u = sympy.Dummy()\n" +
            "        f = sympy.exp(u)\n" +
            "        return ChainRule(ExpRule(f, sympy.E, f, u),\n" +
            "                         exp, u, diff_steps(exp, symbol), expr, symbol)\n" +
            "\n" +
            "\n" +
            "def log_rule(derivative):\n" +
            "    expr, symbol = derivative\n" +
            "    arg = expr.args[0]\n" +
            "    if len(expr.args) == 2:\n" +
            "        base = expr.args[1]\n" +
            "    else:\n" +
            "        base = sympy.E\n" +
            "        if isinstance(arg, sympy.Symbol):\n" +
            "            return LogRule(arg, base, expr, symbol)\n" +
            "        else:\n" +
            "            u = sympy.Dummy()\n" +
            "            return ChainRule(LogRule(u, base, sympy.log(u, base), u),\n" +
            "                             arg, u, diff_steps(arg, symbol), expr, symbol)\n" +
            "\n" +
            "\n" +
            "def function_rule(derivative):\n" +
            "    return FunctionRule(derivative.expr, derivative.symbol)\n" +
            "\n" +
            "\n" +
            "@evaluates(ConstantRule)\n" +
            "def eval_constant(*args):\n" +
            "    return 0\n" +
            "\n" +
            "\n" +
            "@evaluates(ConstantTimesRule)\n" +
            "def eval_constanttimes(constant, other, substep, expr, symbol):\n" +
            "    return constant * diff(substep)\n" +
            "\n" +
            "\n" +
            "@evaluates(AddRule)\n" +
            "def eval_add(substeps, expr, symbol):\n" +
            "    results = [diff(step) for step in substeps]\n" +
            "    return sum(results)\n" +
            "\n" +
            "\n" +
            "@evaluates(DivRule)\n" +
            "def eval_div(numer, denom, numerstep, denomstep, expr, symbol):\n" +
            "    d_numer = diff(numerstep)\n" +
            "    d_denom = diff(denomstep)\n" +
            "    return (denom * d_numer - numer * d_denom) / (denom ** 2)\n" +
            "\n" +
            "\n" +
            "@evaluates(ChainRule)\n" +
            "def eval_chain(substep, inner, u_var, innerstep, expr, symbol):\n" +
            "    return diff(substep).subs(u_var, inner) * diff(innerstep)\n" +
            "\n" +
            "\n" +
            "@evaluates(PowerRule)\n" +
            "@evaluates(ExpRule)\n" +
            "@evaluates(LogRule)\n" +
            "@evaluates(DontKnowRule)\n" +
            "@evaluates(FunctionRule)\n" +
            "def eval_default(*args):\n" +
            "    func, symbol = args[-2], args[-1]\n" +
            "\n" +
            "    if isinstance(func, sympy.Symbol):\n" +
            "        func = sympy.Pow(func, 1, evaluate=False)\n" +
            "\n" +
            "    # Automatically derive and apply the rule (don't use diff() directly as\n" +
            "    # chain rule is a separate step)\n" +
            "    substitutions = []\n" +
            "    mapping = {}\n" +
            "    constant_symbol = sympy.Dummy()\n" +
            "    for arg in func.args:\n" +
            "        if symbol in arg.free_symbols:\n" +
            "            mapping[symbol] = arg\n" +
            "            substitutions.append(symbol)\n" +
            "        else:\n" +
            "            mapping[constant_symbol] = arg\n" +
            "            substitutions.append(constant_symbol)\n" +
            "\n" +
            "    rule = func.func(*substitutions).diff(symbol)\n" +
            "    return rule.subs(mapping)\n" +
            "\n" +
            "\n" +
            "@evaluates(MulRule)\n" +
            "def eval_mul(terms, substeps, expr, symbol):\n" +
            "    diffs = list(map(diff, substeps))\n" +
            "\n" +
            "    result = sympy.S.Zero\n" +
            "    for i in range(len(terms)):\n" +
            "        subresult = diffs[i]\n" +
            "        for index, term in enumerate(terms):\n" +
            "            if index != i:\n" +
            "                subresult *= term\n" +
            "        result += subresult\n" +
            "    return result\n" +
            "\n" +
            "\n" +
            "@evaluates(TrigRule)\n" +
            "def eval_default_trig(*args):\n" +
            "    return sympy.trigsimp(eval_default(*args))\n" +
            "\n" +
            "\n" +
            "@evaluates(RewriteRule)\n" +
            "def eval_rewrite(rewritten, substep, expr, symbol):\n" +
            "    return diff(substep)\n" +
            "\n" +
            "\n" +
            "@evaluates(AlternativeRule)\n" +
            "def eval_alternative(alternatives, expr, symbol):\n" +
            "    return diff(alternatives[1])\n" +
            "\n" +
            "\n" +
            "def diff_steps(expr, symbol):\n" +
            "    deriv = DerivativeInfo(expr, symbol)\n" +
            "\n" +
            "    def key(deriv):\n" +
            "        expr = deriv.expr\n" +
            "        if isinstance(expr, TrigonometricFunction):\n" +
            "            return TrigonometricFunction\n" +
            "        elif isinstance(expr, AppliedUndef):\n" +
            "            return AppliedUndef\n" +
            "        elif not expr.has(symbol):\n" +
            "            return 'constant'\n" +
            "        else:\n" +
            "            return expr.func\n" +
            "\n" +
            "    return switch(key, {\n" +
            "        sympy.Pow: power_rule,\n" +
            "        sympy.Symbol: power_rule,\n" +
            "        sympy.Dummy: power_rule,\n" +
            "        sympy.Add: add_rule,\n" +
            "        sympy.Mul: mul_rule,\n" +
            "        TrigonometricFunction: trig_rule,\n" +
            "        sympy.exp: exp_rule,\n" +
            "        sympy.log: log_rule,\n" +
            "        AppliedUndef: function_rule,\n" +
            "        'constant': constant_rule\n" +
            "    })(deriv)\n" +
            "\n" +
            "\n" +
            "def diff(rule):\n" +
            "    try:\n" +
            "        return evaluators[rule.__class__](*rule)\n" +
            "    except KeyError:\n" +
            "        raise ValueError(\"Cannot evaluate derivative\")\n" +
            "\n" +
            "\n" +
            "class DiffPrinter(object):\n" +
            "    def __init__(self, rule):\n" +
            "        self.print_rule(rule)\n" +
            "        self.rule = rule\n" +
            "\n" +
            "    def print_rule(self, rule):\n" +
            "        if isinstance(rule, PowerRule):\n" +
            "            self.print_Power(rule)\n" +
            "        elif isinstance(rule, ChainRule):\n" +
            "            self.print_Chain(rule)\n" +
            "        elif isinstance(rule, ConstantRule):\n" +
            "            self.print_Number(rule)\n" +
            "        elif isinstance(rule, ConstantTimesRule):\n" +
            "            self.print_ConstantTimes(rule)\n" +
            "        elif isinstance(rule, AddRule):\n" +
            "            self.print_Add(rule)\n" +
            "        elif isinstance(rule, MulRule):\n" +
            "            self.print_Mul(rule)\n" +
            "        elif isinstance(rule, DivRule):\n" +
            "            self.print_Div(rule)\n" +
            "        elif isinstance(rule, TrigRule):\n" +
            "            self.print_Trig(rule)\n" +
            "        elif isinstance(rule, ExpRule):\n" +
            "            self.print_Exp(rule)\n" +
            "        elif isinstance(rule, LogRule):\n" +
            "            self.print_Log(rule)\n" +
            "        elif isinstance(rule, DontKnowRule):\n" +
            "            self.print_DontKnow(rule)\n" +
            "        elif isinstance(rule, AlternativeRule):\n" +
            "            self.print_Alternative(rule)\n" +
            "        elif isinstance(rule, RewriteRule):\n" +
            "            self.print_Rewrite(rule)\n" +
            "        elif isinstance(rule, FunctionRule):\n" +
            "            self.print_Function(rule)\n" +
            "        else:\n" +
            "            self.append(repr(rule))\n" +
            "\n" +
            "    def print_Power(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"Aplicando la regla de potencia a: {0} se obtiene {1}\".format(\n" +
            "                self.format_math(rule.context),\n" +
            "                self.format_math(diff(rule))))\n" +
            "\n" +
            "    def print_Number(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"La derivada de la constante {} es cero.\".format(\n" +
            "                self.format_math(rule.number)))\n" +
            "\n" +
            "    def print_ConstantTimes(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"La derivada de N veces una función \"\n" +
            "                        \"es N veces la derivada de la función\")\n" +
            "            with self.new_level():\n" +
            "                self.print_rule(rule.substep)\n" +
            "            self.append(\"Así, el resultado es: {}\".format(\n" +
            "                self.format_math(diff(rule))))\n" +
            "\n" +
            "    def print_Add(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"Diferenciando {} término por término:\".format(\n" +
            "                self.format_math(rule.context)))\n" +
            "            with self.new_level():\n" +
            "                for substep in rule.substeps:\n" +
            "                    self.print_rule(substep)\n" +
            "            self.append(\"El resultado es: {}\".format(\n" +
            "                self.format_math(diff(rule))))\n" +
            "\n" +
            "    def print_Mul(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"Aplicando la regla del producto:\".format(\n" +
            "                self.format_math(rule.context)))\n" +
            "\n" +
            "            fnames = list(map(lambda n: sympy.Function(n)(rule.symbol),\n" +
            "                              functionnames(len(rule.terms))))\n" +
            "            derivatives = list(map(lambda f: sympy.Derivative(f, rule.symbol), fnames))\n" +
            "            ruleform = []\n" +
            "            for index in range(len(rule.terms)):\n" +
            "                buf = []\n" +
            "                for i in range(len(rule.terms)):\n" +
            "                    if i == index:\n" +
            "                        buf.append(derivatives[i])\n" +
            "                    else:\n" +
            "                        buf.append(fnames[i])\n" +
            "                ruleform.append(reduce(lambda a, b: a * b, buf))\n" +
            "            self.append(self.format_math_display(\n" +
            "                sympy.Eq(sympy.Derivative(reduce(lambda a, b: a * b, fnames),\n" +
            "                                          rule.symbol),\n" +
            "                         sum(ruleform))))\n" +
            "\n" +
            "            for fname, deriv, term, substep in zip(fnames, derivatives,\n" +
            "                                                   rule.terms, rule.substeps):\n" +
            "                self.append(\"{}; para hallar {}:\".format(\n" +
            "                    self.format_math(sympy.Eq(fname, term)),\n" +
            "                    self.format_math(deriv)\n" +
            "                ))\n" +
            "                with self.new_level():\n" +
            "                    self.print_rule(substep)\n" +
            "\n" +
            "            self.append(\"El resultado es: \" + self.format_math(diff(rule)))\n" +
            "\n" +
            "    def print_Div(self, rule):\n" +
            "        with self.new_step():\n" +
            "            f, g = rule.numerator, rule.denominator\n" +
            "            fp, gp = f.diff(rule.symbol), g.diff(rule.symbol)\n" +
            "            x = rule.symbol\n" +
            "            ff = sympy.Function(\"f\")(x)\n" +
            "            gg = sympy.Function(\"g\")(x)\n" +
            "            qrule_left = sympy.Derivative(ff / gg, rule.symbol)\n" +
            "            qrule_right = sympy.ratsimp(sympy.diff(sympy.Function(\"f\")(x) /\n" +
            "                                                   sympy.Function(\"g\")(x)))\n" +
            "            qrule = sympy.Eq(qrule_left, qrule_right)\n" +
            "            self.append(\"Aplicando la regla del cociente que es:\")\n" +
            "            self.append(self.format_math_display(qrule))\n" +
            "            self.append(\"{} y {}.\".format(self.format_math(sympy.Eq(ff, f)),\n" +
            "                                          self.format_math(sympy.Eq(gg, g))))\n" +
            "            self.append(\"Para hallar {}:\".format(self.format_math(ff.diff(rule.symbol))))\n" +
            "            with self.new_level():\n" +
            "                self.print_rule(rule.numerstep)\n" +
            "            self.append(\"Para hallar {}:\".format(self.format_math(gg.diff(rule.symbol))))\n" +
            "            with self.new_level():\n" +
            "                self.print_rule(rule.denomstep)\n" +
            "            self.append(\"Sutituyendo en la regla del cociente:\")\n" +
            "            self.append(self.format_math(diff(rule)))\n" +
            "\n" +
            "    def print_Chain(self, rule):\n" +
            "        with self.new_step(), self.new_u_vars() as (u, du):\n" +
            "            self.append(\"Sea {}.\".format(self.format_math(sympy.Eq(u, rule.inner))))\n" +
            "            self.print_rule(replace_u_var(rule.substep, rule.u_var, u))\n" +
            "        with self.new_step():\n" +
            "            if isinstance(rule.innerstep, FunctionRule):\n" +
            "                self.append(\n" +
            "                    \"Entonces, aplicando la regla de la cadena. Multipicamos por {}:\".format(\n" +
            "                        self.format_math(\n" +
            "                            sympy.Derivative(rule.inner, rule.symbol))))\n" +
            "                self.append(self.format_math_display(diff(rule)))\n" +
            "            else:\n" +
            "                self.append(\n" +
            "                    \"Entonces, aplicando la regla de la cadena. Multipicamos por {}:\".format(\n" +
            "                        self.format_math(\n" +
            "                            sympy.Derivative(rule.inner, rule.symbol))))\n" +
            "                with self.new_level():\n" +
            "                    self.print_rule(rule.innerstep)\n" +
            "                self.append(\"El resultado de aplicar la regla de la cadena:\")\n" +
            "                self.append(self.format_math_display(diff(rule)))\n" +
            "\n" +
            "    def print_Trig(self, rule):\n" +
            "        with self.new_step():\n" +
            "            if isinstance(rule.f, sympy.sin):\n" +
            "                self.append(\"La derivada del seno es el coseno:\")\n" +
            "            elif isinstance(rule.f, sympy.cos):\n" +
            "                self.append(\"La derivada del coseno es el negativo del seno:\")\n" +
            "            elif isinstance(rule.f, sympy.sec):\n" +
            "                self.append(\"La derivada de la secante es secante por tangente:\")\n" +
            "            elif isinstance(rule.f, sympy.csc):\n" +
            "                self.append(\"La derivada de la cosecante es el negativo de la cosecante por la cotangente:\")\n" +
            "            self.append(\"{}\".format(\n" +
            "                self.format_math_display(sympy.Eq(\n" +
            "                    sympy.Derivative(rule.f, rule.symbol),\n" +
            "                    diff(rule)))))\n" +
            "\n" +
            "    def print_Exp(self, rule):\n" +
            "        with self.new_step():\n" +
            "            if rule.base == sympy.E:\n" +
            "                self.append(\"La derivada de {} es ella misma.\".format(\n" +
            "                    self.format_math(sympy.exp(rule.symbol))))\n" +
            "            else:\n" +
            "                self.append(\n" +
            "                    self.format_math(sympy.Eq(sympy.Derivative(rule.f, rule.symbol),\n" +
            "                                              diff(rule))))\n" +
            "\n" +
            "    def print_Log(self, rule):\n" +
            "        with self.new_step():\n" +
            "            if rule.base == sympy.E:\n" +
            "                self.append(\"La derivada de {} es {}.\".format(\n" +
            "                    self.format_math(rule.context),\n" +
            "                    self.format_math(diff(rule))\n" +
            "                ))\n" +
            "            else:\n" +
            "                # This case shouldn't come up often, seeing as SymPy\n" +
            "                # automatically applies the change-of-base identity\n" +
            "                self.append(\"La derivada de {} es {}.\".format(\n" +
            "                    self.format_math(sympy.log(rule.symbol, rule.base,\n" +
            "                                               evaluate=False)),\n" +
            "                    self.format_math(1 / (rule.arg * sympy.ln(rule.base)))))\n" +
            "                self.append(\"Por lo tanto {}\".format(\n" +
            "                    self.format_math(sympy.Eq(\n" +
            "                        sympy.Derivative(rule.context, rule.symbol),\n" +
            "                        diff(rule)))))\n" +
            "\n" +
            "    def print_Alternative(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"Hay muchas formas de efectuar la derivada.\")\n" +
            "            self.append(\"Una forma:\")\n" +
            "            with self.new_level():\n" +
            "                self.print_rule(rule.alternatives[0])\n" +
            "\n" +
            "    def print_Rewrite(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"Reescribimos la función para ser derivada:\")\n" +
            "            self.append(self.format_math_display(\n" +
            "                sympy.Eq(rule.context, rule.rewritten)))\n" +
            "            self.print_rule(rule.substep)\n" +
            "\n" +
            "    def print_Function(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"Trivial:\")\n" +
            "            self.append(self.format_math_display(\n" +
            "                sympy.Eq(sympy.Derivative(rule.context, rule.symbol),\n" +
            "                         diff(rule))))\n" +
            "\n" +
            "    def print_DontKnow(self, rule):\n" +
            "        with self.new_step():\n" +
            "            self.append(\"Don't know the steps in finding this derivative.\")\n" +
            "            self.append(\"But the derivative is\")\n" +
            "            self.append(self.format_math_display(diff(rule)))\n" +
            "\n" +
            "\n" +
            "class HTMLPrinter(DiffPrinter, HTMLPrinterP):\n" +
            "    def __init__(self, rule):\n" +
            "        self.alternative_functions_printed = set()\n" +
            "        HTMLPrinterP.__init__(self)\n" +
            "        DiffPrinter.__init__(self, rule)\n" +
            "\n" +
            "    def print_Alternative(self, rule):\n" +
            "        if rule.context.func in self.alternative_functions_printed:\n" +
            "            self.print_rule(rule.alternatives[0])\n" +
            "        elif len(rule.alternatives) == 2:\n" +
            "            self.alternative_functions_printed.add(rule.context.func)\n" +
            "            self.print_rule(rule.alternatives[1])\n" +
            "        else:\n" +
            "            self.alternative_functions_printed.add(rule.context.func)\n" +
            "            with self.new_step():\n" +
            "                self.append(\"Hay muchas formas de efectuar la derivada.\")\n" +
            "                for index, r in enumerate(rule.alternatives[1:]):\n" +
            "                    with self.new_collapsible():\n" +
            "                        self.append_header(\"Método #{}\".format(index + 1))\n" +
            "                        with self.new_level():\n" +
            "                            self.print_rule(r)\n" +
            "\n" +
            "    def finalize(self):\n" +
            "        answer = diff(self.rule)\n" +
            "        if answer:\n" +
            "            simp = sympy.simplify(answer)\n" +
//            "            simp = sympy.trigsimp(simp).rewrite(sin,cos)\n" +
            "            simp = sympy.trigsimp(simp)\n" +
            "            simp = sympy.factor(simp)\n" +
            "            if simp != answer:\n" +
            "                answer = simp\n" +
            "                with self.new_step():\n" +
            "                    self.append(\"Simplificando:\")\n" +
            "                    self.append(self.format_math_display(simp))\n" +
            "            else:\n"+
            "                simp = sympy.expand(answer)\n"+
//            "                simp = sympy.trigsimp(simp).rewrite(sin,cos)\n"+
            "                simp = sympy.trigsimp(simp)\n"+
            "                simp = sympy.factor(simp)\n"+
            "                if simp != answer:\n" +
            "                    answer = simp\n" +
            "                    with self.new_step():\n" +
            "                        self.append(\"Simplificando:\")\n" +
            "                        self.append(self.format_math_display(simp))\n" +
            "        self.lines.append('</ol><br/>')\n" +
            "        self.lines.append('<hr/>')\n" +
            "        self.level = 0\n" +
            "        self.append('La respuesta es:')\n" +
            "        self.append(self.format_math_display(answer))\n" +
            "        return '\\n'.join(self.lines)\n" +
            "\n" +
            "\n" +
            "def print_html_steps(function, symbol):\n" +
            "    a = HTMLPrinter(diff_steps(function, symbol))\n" +
            "    return a.finalize()\n" +
            "\n" +
            "\n" +
            "\n" +
            "def acomodaNotacion(expresion):\n" +
            "    # parche para notación\n" +
            "    expresion = expresion.replace(\"\\\\frac{d}{d x} f{\\\\left(x \\\\right)} g{\\\\left(x \\\\right)}\",\n" +
            "                                  \"\\\\frac{d}{d x}( f{\\\\left(x \\\\right)} g{\\\\left(x \\\\right)})\")\n"+
            "    expresion = expresion.replace(\"\\\\frac{d}{d x} \\\\frac{f{\\\\left(x \\\\right)}}{g{\\left(x \\\\right)}}\",\n" +
            "                                  \"\\\\frac{d}{d x}( \\\\frac{f{\\\\left(x \\\\right)}}{g{\\left(x \\\\right)}})\")\n" +
            "    expresion = expresion.replace(\n" +
            "        \"\\\\frac{- f{\\\\left(x \\\\right)} \\\\frac{d}{d x} g{\\\\left(x \\\\right)} + g{\\\\left(x \\\\right)} \\\\frac{d}{d x} f{\\\\left(x \\\\right)}}{g^{2}{\\\\left(x \\\\right)}}\",\n" +
            "        \"\\\\frac{- f{\\\\left(x \\\\right)} \\\\frac{d}{d x}( g{\\\\left(x \\\\right)}) + g{\\\\left(x \\\\right)} \\\\frac{d}{d x}( f{\\\\left(x \\\\right))}}{g^{2}{\\\\left(x \\\\right)}}\")\n" +
            "    expresion = expresion.replace(\"\\\\frac{d}{d x} f{\\\\left(x \\\\right)}\",\n" +
            "                                \"\\\\frac{d}{d x}( f{\\\\left(x \\\\right)})\")\n" +
            "    expresion = expresion.replace(\"\\\\frac{d}{d x} g{\\\\left(x \\\\right)}\",\n" +
            "                                \"\\\\frac{d}{d x}( g{\\\\left(x \\\\right)})\")\n" +
            "    return expresion\n" +
            "\n"+
            "\n" ;
    /**
     * Contiene el punto de entrada del script de Python para procesar la expresión
     * 1.- Abre un archivo de texto para guardar la salida <p>
     * 2.- Define "x" como un símbolo para poder derivar expresiones respecto a ella. <p>
     * 3.- Se convierte la expresión en LaTeX an una expresión Sympy, definiendo "pi"
     * como un símbolo reconocible <p>
     * Se debe poder definir como símbolo otra variable diferente de "x" , como "t" por ejemplo
     * @author Iván Gutierrez
     *
     */
    private static final String PARSER =                 "##MAIN##\n" +
            "\n" +
            "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
            "$VARIABLEIND$ = symbols(\'$VARIABLEIND$\')\n" +
            "expr = parse_latex(r\"$EXPRESION$\").subs({Symbol('pi'): pi})\n";
    /**
     * Escribe en el archivo de salida, abierto en el fragmento PARSER, el problema
     * y la solución en html/MathJax invocando print_html_steps contenida en el fragmento
     * DERIVADOR
     * <p>
     * acomodaNotacion pone paréntesis explícitos para evitar ambigüedades en expresiones
     */
    private static final String SOLVER =
            "salida.write(\"Obtener: $$%s$$<br><br>\" % latex(Derivative(expr,$VARIABLEIND$)))\n" +
                    "solucion = print_html_steps(expr, $VARIABLEIND$)\n" +
                    "solucion=acomodaNotacion(solucion)\n"+
                    "salida.write(solucion)\n" ;
    /**
     * Scriptlet para obtener la tangente a una curva en un punto
     * <p>
     * 1.-Se deriva la expresión, como en el scriptlet del SOLVER,
     * escribiendo el procedimiento en el archivo de salida.
     * <p>
     * 2.-Se evalúa la derivada en el punto solicitado y se escribe la solución.
     */
    private static final String SOLVER_RECTA_TANGENTE =
            "x0 = $X0$\n"
                    +"salida.write(\"Obtener: $$%s$$<br><br>\" % latex(Derivative(expr, x)))\n"
                    +"solucion = print_html_steps(expr, $VARIABLEIND$)\n"
                    +"solucion = acomodaNotacion(solucion)\n"
                    +"salida.write(solucion)\n"
                    +"derivada = Derivative(expr)\n"
                    +"y_0 = expr.subs(x, x0)\n"
                    +"yp_0 = derivada.subs(x, x0)\n"
                    +"salida.write(\"\\n Evaluamos en $$x_{0}=$X0$ $$\\n<br/><br/>\")\n"
                    + "salida.write(\" Sustituyendo en $$f(x)$$ el valor de $$x_{0}$$<br/><center>$$f(x_{0})=%s$$</center> \\n<br/><br/>\" % latex(y_0))\n" +
                    "solucion=\"Sustituyendo en $$f'(x)$$ el valor de $$x_{0}$$<br/><center>$$f'(x_{0})=%s=%s$$</center> \\n<br/><br/>\" % (latex(yp_0), latex(yp_0.doit()))\n" +
                    "solucion=solucion.replace(\"+-\",\"-\")\n" +
                    "solucion = solucion.replace(\"--\",\"+\")\n" +
                    "salida.write(solucion)\n"
                    +"salida.write(\"Sustituyendo en la ecuación de la recta $$y-f(x_{0})=f'(x_{0})(x-x_{0})$$ obtenemos:\\n<br/><center>$$y-(%s)=(%s)(x-(%s))$$ </center>\\n<br/>\" % (\n"
                    +"latex(y_0.doit()), latex(yp_0.doit()), x0))\n"
                    +"solucion=\"Simplificando:\\n<br/><center>$$y=%sx+%s$$</center><br/><br/>\"%(latex(yp_0.doit()),latex(y_0.doit()-yp_0.doit()*x0))\n"
                    +"solucion=solucion.replace(\"+-\",\"-\")\n"
                    +"salida.write(solucion)\n"
            ;
    /**
     * Scriplet para obtener la tangente horizontal.<p>
     * 1. Se deriva la expresión.
     * 2. Su iguala con cero y se resuelve.
     * 3. Con las soluciones se sustituye en la ecuación original para encontrar
     * los puntos.
     * @author Iván Gutiérrez
     */
    private static final String SOLVER_TANGENTE_HORIZONTAL =
            "salida.write(\"Obtener: $$%s$$<br><br>\" % latex(Derivative(expr, $VARIABLEIND$)))\n" +
                    "solucion = print_html_steps(expr, $VARIABLEIND$)\n" +
                    "solucion = acomodaNotacion(solucion)\n" +
                    "salida.write(solucion)\n" +
                    "derivada = Derivative(expr)\n" +
                    "derivada = derivada.doit()\n" +
                    "anula = solve(derivada, $VARIABLEIND$)\n" +
                    "solucion = \"Resolviendo $$%s=0$$ obtenemos las raices<br/>\" % (latex(derivada))\n" +
                    "n = 1\n" +
                    "for x_0 in anula:\n" +
                    "    solucion = solucion + \"$$x_%s=%s$$ <br/>\" % (n, latex(x_0))\n" +
                    "    n = n+1\n" +
                    "n = 1\n" +
                    "solucion = solucion+\"Sustituyendo en $$%s$$, se obtienen los puntos:<br/>\"%(latex(expr))\n" +
                    "for x_0 in anula:\n" +
                    "    y = expr.subs(x, x_0)\n" +
                    "    solucion = solucion + \"$$P_%s(%s,%s)$$<br/>\" % (n,latex(x_0), latex(y))\n" +
                    "    n = n+1\n" +
                    "\n" +
                    "salida.write(solucion)\n"
            ;
    /**
     * Scriplet para obtener la tangente horizontal.<p>
     * 1. Se deriva la expresión.
     * 2. Su iguala con cero y se resuelve.
     * 3. Con las soluciones se sustituye en la ecuación original para encontrar
     * los puntos.
     * 4. Construye una gráfica con el plug-in JSX (JavaScript) del problema y lo incluye en la solución
     * JSX Graphs funciona en JavaScript
     * ES NECESARIO INSTALAR EN EL SERVIDOR DE MOODLE EL PLUG-IN JSX
     * @author Iván Gutiérrez
     */
    private static final String SOLVER_TANGENTE_HORIZONTAL_GRAFICA_JSX =
            /* hay que instalar seaborn y matplotlib para que esta parte funcione bien*/
            "salida.write(\"Obtener: $$%s$$<br><br>\" % latex(Derivative(expr, $VARIABLEIND$)))\n" +
                    "solucion = print_html_steps(expr, $VARIABLEIND$)\n" +
                    "solucion = acomodaNotacion(solucion)\n" +
                    "salida.write(solucion)\n" +
                    "derivada = Derivative(expr)\n" +
                    "derivada = derivada.doit()\n" +
                    "anula = solve(derivada, x)\n" +
                    "puntos = []\n" +
                    "xmin,xmax = 100,-100\n" +
                    "ymin,ymax = 100,-100\n" +
                    "solucion = \"Resolviendo $$%s=0$$ obtenemos las raices<br/>\" % (latex(derivada))\n" +
                    "n = 1\n" +
                    "for x_0 in anula:\n" +
                    "    solucion = solucion + \"$$x_%s=%s$$ <br/>\" % (n, latex(x_0))\n" +
                    "    n = n+1\n" +
                    "n = 1\n" +
                    "solucion = solucion+\"Sustituyendo en $$%s$$, se obtienen los puntos:<br/>\"%(latex(expr))\n" +
                    "for x_0 in anula:\n" +
                    "    y = expr.subs(x, x_0)\n" +
                    "    puntos.append([x_0,y])\n" +
                    "    if x_0 > xmax:\n" +
                    "        xmax=x_0\n" +
                    "    if x_0 < xmin:\n" +
                    "        xmin=x_0\n" +
                    "    if y > ymax:\n" +
                    "        ymax=y\n" +
                    "    if y < ymin:\n" +
                    "        ymin=y\n" +
                    "    solucion = solucion + \"$$P_%s(%s,%s)$$<br/>\" % (n,latex(x_0), latex(y))\n" +
                    "    n = n+1\n" +
                    "if (xmax-xmin) < (ymax-ymin):\n" +
                    "    intervalo = ymax-ymin\n" +
                    "else:\n" +
                    "    intervalo = xmax-xmin\n" +
                    "intervalo=round(intervalo*6/10)+1\n"+
                    "centrox = (xmax + xmin)/2\n" +
                    "centroy = (ymax + ymin)/2\n" +
                    "xmin = centrox-intervalo\n" +
                    "xmax = centrox+intervalo\n" +
                    "ymin = centroy-intervalo\n" +
                    "ymax = centroy+intervalo\n" +
                    "salida.write(solucion)\n" +
                    "salida.write('\\n<jsxgraph width=\"600\" height=\"500\">\\n')\n" +
                    "funcion = 'function f(x) { return '+str(expr)+'; }\\n '\n" +
                    "salida.write(funcion)\n"+
                    "\n"+
                    "salida.write('var brd = JXG.JSXGraph.initBoard(BOARDID, {" +
                    "boundingbox:['+str(xmin)+','+str(ymax)+','+str(xmax)+','+str(ymin)+'], axis:true});\\n')\n" +
                    "i = 0\n"+
                    "for xt, yt in puntos:\n"+
                    "    i = i + 1\n"+
                    "    nombrepunto=\"\'P\" + str(i)+\"'\"\n"+
                    "    salida.write('var p = brd.create(\"point\", ['+str(xt)+','+str(yt)+'],{name:' + nombrepunto + ',fixed:true});\\n')\n" +
                    "    salida.write('var l = brd.create(\"functiongraph\",[function(x){ return '+str(yt)+';}]);\\n')\n" +
                    "salida.write('var c = brd.create(\"functiongraph\", f, {strokewidth:2});\\n')\n" +
                    "salida.write('</jsxgraph>\\n')\n"
            ;
    /**
     * Scriptlet para obtener la tangente a una curva en un punto y graficar
     * <p>
     * 1.-Se deriva la expresión, como en el scriptlet del SOLVER,
     * escribiendo el procedimiento en el archivo de salida.
     * <p>
     * 2.-Se evalúa la derivada en el punto solicitado y se escribe la solución.
     * 3.-Se genera grafica SVG
     * @author Iván Gutiérrez
     */
    private static final String SOLVER_RECTA_TANGENTE_GRAFICA =
            "x0 = $X0$\n"
                    +"salida.write(\"Obtener: $$%s$$<br><br>\" % latex(Derivative(expr, x)))\n"
                    +"solucion = print_html_steps(expr, $VARIABLEIND$)\n"
                    +"solucion = acomodaNotacion(solucion)\n"
                    +"salida.write(solucion)\n"
                    +"derivada = Derivative(expr)\n"
                    +"y_0 = expr.subs(x, x0)\n"
                    +"yp_0 = derivada.subs(x, x0)\n"
                    +"salida.write(\"\\n Evaluamos en $$x_{0}=$X0$ $$\\n<br/><br/>\")\n"
                    + "salida.write(\" Sustituyendo en $$f(x)$$ el valor de $$x_{0}$$<br/><center>$$f(x_{0})=%s$$</center> \\n<br/><br/>\" % latex(y_0))\n" +
                    "solucion=\"Sustituyendo en $$f'(x)$$ el valor de $$x_{0}$$<br/><center>$$f'(x_{0})=%s=%s$$</center> \\n<br/><br/>\" % (latex(yp_0), latex(yp_0.doit()))\n" +
                    "solucion=solucion.replace(\"+-\",\"-\")\n" +
                    "solucion = solucion.replace(\"--\",\"+\")\n" +
                    "salida.write(solucion)\n"
                    +"salida.write(\"Sustituyendo en la ecuación de la recta $$y-f(x_{0})=f'(x_{0})(x-x_{0})$$ obtenemos:\\n<br/><center>$$y-(%s)=(%s)(x-(%s))$$ </center>\\n<br/>\" % (\n"
                    +"latex(y_0.doit()), latex(yp_0.doit()), x0))\n"
                    +"solucion=\"Simplificando:\\n<br/><center>$$y=%sx+%s$$</center><br/><br/>\"%(latex(yp_0.doit()),latex(y_0.doit()-yp_0.doit()*x0))\n"
                    +"solucion=solucion.replace(\"+-\",\"-\")\n"
                    +"salida.write(solucion)\n"
                    +"xmin=x0-5\n" +
                    "xmax=x0+5\n" +
                    "ymin=y_0-5\n" +
                    "ymax=y_0+5\n" +
                    "m=yp_0.doit()\n" +
                    "b=y_0.doit()-yp_0.doit()*x0\n" +
                    "tanexpr = str(m)+'*x+('+str(b)+')'\n" +
                    "salida.write('\\n<jsxgraph width=\"600\" height=\"500\">\\n')\n" +
                    "funcion = 'function f(x) { return '+ str(expr)+'; }\\n '\n" +
                    "tangente= 'function t(x) { return '+ tanexpr+'; }\\n'\n" +
                    "salida.write(funcion)\n" +
                    "salida.write(tangente)\n" +
                    "\n" +
                    "salida.write('var brd = JXG.JSXGraph.initBoard(BOARDID, {boundingbox:['+str(xmin)+','+str(ymax)+','+str(xmax)+','+str(ymin)+'], axis:true});\\n')\n" +
                    "salida.write('var p = brd.create(\"point\", ['+str(x0)+','+str(y_0)+'],{name:\\'P0\\',fixed:true});\\n')\n" +
                    "salida.write('var l = brd.create(\"functiongraph\", t, {strokecolor:\\'green\\'});\\n')\n"+
                    "salida.write('var c = brd.create(\"functiongraph\", f, {strokewidth:2});\\n')\n" +
                    "salida.write('</jsxgraph>\\n')\n"
            ;

    /**
     * Scriptlet de Python para efectuar derivadas sucesivas.
     * Se concatena sucesivamente para obtener derivadas de mayor orden
     * @author Iván Gutiérrez
     */
    private static final String DIFF_STEP =
            "derivada = Derivative(expr)\n"+
                    "derivada = factor(derivada.doit())\n"+
                    "salida.write(\"<br/>Siguiente derivada<br/>Obtener: $$%s$$<br><br>\" % latex(Derivative(derivada,$VARIABLEIND$)))\n" +
                    "solucion = print_html_steps(derivada, $VARIABLEIND$)\n" +
                    "solucion=acomodaNotacion(solucion)\n"+
                    "expr = derivada\n" +
                    "salida.write(solucion)\n" ;
    /**
     * Cierra el archivo en el que se escribe la salida.
     * Debe ser el último elemento que se concatena para
     * armar un script con los scriptlets previos
     * @author Iván Gutiérrez
     */
    private static final String CLOSER      =                      "salida.close()\n";


    /**
     * Recibe un script en python, lo ejecuta y regresa la salida de la ejecución.
     * Está pensado para un ambiente Linux, escribe y lee de /tmp.
     * Asume que Python3 y sus bibliotecas están instalados.
     * Usa randomUUID para evitar colisiones si se ejecuta en forma simultanea
     * con otros generadores del proyecto.
     *
     * @param code String con script en Python para ejecutar.
     * @return String con el resultado de la ejecución.
     * @author Iván Gutierrez
     */
    private static String ejecutaPython(String code) {
        String uuid = String.valueOf(UUID.randomUUID());
        String solucion = "";
        code = code.replace("$UUID$", uuid);
        try (FileWriter scriptFile = new FileWriter("/tmp/script_" + uuid + ".py");
             BufferedWriter bw = new BufferedWriter(scriptFile)) {
            bw.write(code);
        } catch (IOException e) {
            System.err.format("Escribiendo Script IOException: %s%n", e);
        }
        String cmd = "python3 /tmp/script_" + uuid + ".py";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream errorPython = p.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(errorPython, StandardCharsets.UTF_8));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println("Python: " + line);
                }
            } catch (IOException e) {
                System.out.println("Exception in reading output" + e.toString());
            }
            int exitVal = p.waitFor();
            if (exitVal == 0) System.out.println("Ok");
        } catch (java.io.IOException e) {
            System.err.format("Ejecutando Python IOException: %s%n", e);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            solucion = new String(Files.readAllBytes(Paths.get("/tmp/solucion_" + uuid + ".txt")));
            File archivoSolucion = new File("/tmp/solucion_" + uuid + ".txt");
            archivoSolucion.delete();
            File archivoScript = new File("/tmp/script_" + uuid + ".py");
            archivoScript.delete();
        } catch (java.io.IOException e) {
            System.err.format("Leyendo Solucion IOException: %s%n", e);
        }
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd '-' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));
        return solucion;

    }

    /**
     * Hace derivación simbólica de la expresión usando el límite que tiende a cero del incremento
     *
     * @param expresion cadena en LaTex con expresión para derivar
     * @return cadena con el proceso de derivación usando el método del incremento que tiende a cero
     * @see "Cálculo de una Variable, Thomas 12a. Edición cap 3.2"
     * @author Iván Gutiérrez
     */
    public static String incremento(String expresion) {
        String PLANTILLA_SYMPY =
                "from sympy import *\n" +
                        "from sympy.parsing.latex import parse_latex\n" +
                        "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                        "init_printing()\n" +
                        "x = var('x')\n" +
                        "h = var('h')\n" +
                        "f = parse_latex(r\" $EXPRESION$ \")\n" +
                        "fh=f.subs(x,x+h)\n" +
                        "drv=(fh-f)/h\n" +
                        "salida.write(\"$$\\\\displaystyle f(x)=%s$$<br/><br/>\\n\" %(latex(f)))\n" +
                        "salida.write(\"$$\\\\displaystyle f(x+h)=%s$$<br/><br/>\\n\" %latex(fh))\n" +
                        "salida.write(\"$$\\\\displaystyle \\\\frac{f(x+h)-f(x)}{h}=%s=%s $$\\n <br/><br/>\" % (latex(drv),latex(drv.factor())))\n" +
                        "drv=limit(drv,h,0)\n" +
                        "resultado=drv.apart()\n" +
                        "salida.write(\"$$\\\\displaystyle f'(x)=\\\\lim_{h\\\\to 0} \\\\frac{f(x+h)-f(x)}{h}=%s=%s$$<br/><br/>\\n\" % (latex(drv),latex(resultado)))";

        String script = PLANTILLA_SYMPY;
        script = script.replace("$EXPRESION$", expresion);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Deriva simbólicamente expresiones algebráicas en LaTex usando el método de limite que tiende a cero y
     * empleando además la multiplicación por el conjugado del incremento para eliminar la ideterminación
     *
     * @param expresion cadena en LaTex con expresión para derivar
     * @return cadena con el proceso de derivación usando el método del incremento que tiende a cero
     * @see "Cálculo de una Variable, Thomas 12a. Edición cap 3.2"
     * @author Iván Gutiérrez
     */
    public static String solucionaSimbolicoConjugados(String expresion) {
        String PLANTILLA_SYMPY =
                "from sympy import *\n" +
                        "from sympy.parsing.latex import parse_latex\n" +
                        "\n" +
                        "salida = open(\"/tmp/solucion_$UUID$.txt\", \"w\")\n" +
                        "init_printing()\n" +
                        "x = var('x')\n" +
                        "h = var('h')\n" +
                        "f = parse_latex(r\"$EXPRESION$ \")\n" +
                        "fh = f.subs(x, x + h)\n" +
                        "drv = (fh - f) / h\n" +
                        "salida.write(\"$$\\\\displaystyle f(x)=%s$$<br/><br/>\\n\" % (latex(f)))\n" +
                        "salida.write(\"$$\\\\displaystyle f(x+h)=%s$$<br/><br/>\\n\" % latex(fh))\n" +
                        "salida.write(\"$$\\\\displaystyle \\\\frac{f(x+h)-f(x)}{h}=%s=%s $$\\n <br/><br/>Multiplicando por el conjugado<br/><br/>\" % (latex(drv), latex(drv.simplify())))\n" +
                        "recipr = 1 / f\n" +
                        "reciprh = 1 / fh\n" +
                        "conjugado = recipr + reciprh\n" +
                        "numerador = ( fh - f ) * conjugado\n" +
                        "denominador = recipr * reciprh\n" +
                        "nuevoNumerador = numerador.simplify()\n" +
                        "salida.write(\"$$\\\\displaystyle = %s \\\\frac{%s}{%s}$$<br/><br/>\\n\" % (latex(drv), latex(conjugado), latex(conjugado)));\n" +
                        "salida.write(\n" +
                        "    \"$$\\\\displaystyle = \\\\frac{1}{%s({%s})} %s$$<br/><br/>\\n\" % (latex(h), latex(conjugado), latex((fh - f) * conjugado)));\n" +
                        "salida.write(\"$$\\\\displaystyle numerador: %s=%s$$<br/><br/>\\n\" % (latex(numerador), latex(numerador.simplify())));\n" +
                        "salida.write(\"$$\\\\displaystyle simplificando: \\\\frac{f(x+h)-f(x)}{h}=\\\\frac{%s}{h}*\\\\frac{1}{%s} $$<br/><br/>\\n\" % (\n" +
                        "latex(nuevoNumerador), latex(conjugado)));\n" +
                        "drv = nuevoNumerador / h / conjugado\n" +
                        "limite = limit(drv, h, 0)\n" +
                        "salida.write(\"$$\\\\displaystyle f'(x)=\\\\lim_{h\\\\to 0} \\\\frac{f(x+h)-f(x)}{h}=\\\\lim_{h\\\\to 0}%s=%s$$<br/><br/>\\n\" % (latex(drv), latex(limite.simplify())))\n";
        String script = PLANTILLA_SYMPY;
        script = script.replace("$EXPRESION$", expresion);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Deriva simbólicamente expresiones algebráicas en LaTex usando la regla de la cadena
     * se escoge una variable de sustitucion \f$u\f$
     * y se ponen las expresiones en términos de \f$f(u)\f$ y \f$u=g(x)\f$
     *
     * @param fu función de la variable de sustitución  (u)
     * @param gx función de la variable original (x)
     * @return cadena con el proceso de derivación usando la regla de la cadena
     * @see "Cálculo de una Variable, Thomas 12a. Edición cap 3.6"
     * @author Iván Gutiérrez
     * @deprecated derivaSimbolico puede sustituir a esta función.
     */

    public static String reglaCadena(String fu, String gx) {
        String PLANTILLA_SYMPY = "from sympy import *\n" +
                "from sympy.parsing.latex import parse_latex\n" +
                "\n" +
                "\n" +
                "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                "init_printing()\n" +
                "x = var('x')\n" +
                "u = var('u')\n" +
                "f = parse_latex(r\"$FU$\")\n" +
                "g = parse_latex(r\"$GX$\")\n" +
                "salida.write(\"$$\\\\displaystyle f(x)=%s$$<br/><br/>\\n\" % latex(f.subs(u,g)))\n" +
                "salida.write(\"$$\\\\displaystyle f(u)=%s$$<br/><br/>\\n\" % latex(f))\n" +
                "salida.write(\"$$\\\\displaystyle u(x)=%s$$<br/><br/>\\n\" % latex(g))\n" +
                "df = diff(f,u)\n" +
                "dg = diff(g,x)\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}=%s$$<br/><br/>\\n\" % latex(df))\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{du}{dx}=%s$$<br/><br/>\\n\" % latex(dg))\n" +
                "result = df*dg\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}\\\\frac{du}{dx} = %s$$<br/><br/>\\n\" % latex(result))\n" +
                "result = result.subs(u,g)\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{dx}=%s=%s$$<br/><br/>\\n\" % (latex(result), latex(result.cancel())))\n" +
                "salida.close()";
        String script = PLANTILLA_SYMPY;
        script = script.replace("$FU$", fu);
        script = script.replace("$GX$", gx);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Deriva simbólicamente expresiones algebráicas en LaTex usando la regla de la cadena
     * se escoge una variable de sustitucion "u" y se ponen las expresiones en términos de f(u) y u(x)=g(x).
     * A diferencia del método reglaCadena evita una simplificación al final
     *
     * @param fu función de la variable de sustitución  (u)
     * @param gx función de la variable original (x)
     * @return cadena con el proceso de derivación usando la regla de la cadena
     * @see class reglaCadena
     * @deprecated derivaSimbolico puede sustituir a esta función.
     */

    public static String reglaCadenaNoCancel(String fu, String gx) {

        String PLANTILLA_SYMPY = "from sympy import *\n" +
                "from sympy.parsing.latex import parse_latex\n" +
                "\n" +
                "\n" +
                "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                "init_printing()\n" +
                "x = var('x')\n" +
                "u = var('u')\n" +
                "f = parse_latex(r\"$FU$\")\n" +
                "g = parse_latex(r\"$GX$\")\n" +
                "salida.write(\"$$\\\\displaystyle f(x)=%s$$<br/><br/>\\n\" % latex(f.subs(u,g)))\n" +
                "salida.write(\"$$\\\\displaystyle f(u)=%s$$<br/><br/>\\n\" % latex(f))\n" +
                "salida.write(\"$$\\\\displaystyle u(x)=%s$$<br/><br/>\\n\" % latex(g))\n" +
                "df = diff(f,u)\n" +
                "dg = diff(g,x)\n" +
//                "salida.write(\"$$\\\\displaystyle  \\\\frac{df}{du}=%s$$<br/><br/>\\n\" % latex(df))\n" +
//                "salida.write(\"$$ $$\\n\")\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}=%s$$<br/><br/>\\n\" % latex(df))\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{du}{dx}=%s$$<br/><br/>\\n\" % latex(dg))\n" +
                "result = df*dg\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}\\\\frac{du}{dx} = %s$$<br/><br/>\\n\" % latex(result))\n" +
                "result = result.subs(u,g)\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{dx}=%s$$<br/><br/>\\n\" % (latex(result)))\n" +
                "salida.close()";
        String script = PLANTILLA_SYMPY;
        script = script.replace("$FU$", fu);
        script = script.replace("$GX$", gx);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * reglaCadenaTrig deriva simbólicamente expresiones algebráicas en LaTex usando la regla de la cadena
     * se escoge una variable de sustitucion "u" y se ponen las expresiones en términos de f(u) y u(x)=g(x).
     * El método intenta hacer simplificación de expresiones trigonométricas
     *
     * @param fu función de la variable de sustitución  (u)
     * @param gx función de la variable original (x)
     * @return cadena con el proceso de derivación usando la regla de la cadena
     * @see class reglaCadena
     * @author Iván Gutiérrez
     * @deprecated derivaSimbolico puede sustituir a esta función.
     */
    public static String reglaCadenaTrig(String fu, String gx) {

        String PLANTILLA_SYMPY = "from sympy import *\n" +
                "from sympy.parsing.latex import parse_latex\n" +
                "\n" +
                "\n" +
                "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                "init_printing()\n" +
                "x = var('x')\n" +
                "u = var('u')\n" +
                "f = parse_latex(r\"$FU$\")\n" +
                "g = parse_latex(r\"$GX$\")\n" +
                "salida.write(\"$$\\\\displaystyle f(x)=%s$$<br/><br/>\\n\" % latex(f.subs(u,g)))\n" +
                "salida.write(\"$$\\\\displaystyle f(u)=%s$$<br/><br/>\\n\" % latex(f))\n" +
                "salida.write(\"$$\\\\displaystyle u(x)=%s$$<br/><br/>\\n\" % latex(g))\n" +
                "df = diff(f,u)\n" +
                "dg = diff(g,x)\n" +
//                "salida.write(\"$$\\\\displaystyle  \\\\frac{df}{du}=%s$$<br/><br/>\\n\" % latex(df))\n" +
//                "salida.write(\"$$ $$\\n\")\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}=%s$$<br/><br/>\\n\" % latex(df))\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{du}{dx}=%s$$<br/><br/>\\n\" % latex(dg))\n" +
                "result = df*dg\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}\\\\frac{du}{dx} = %s$$<br/><br/>\\n\" % latex(result))\n" +
                "result = result.subs(u,g)\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{dx}=%s=%s$$<br/><br/>\\n\" % (latex(result), latex(trigsimp(result))))\n" +
                "salida.close()";
        String script = PLANTILLA_SYMPY;
        script = script.replace("$FU$", fu);
        script = script.replace("$GX$", gx);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Deriva simbólicamente expresiones algebráicas en LaTex usando la regla de la cadena para una suma
     * de dos productos.
     * Se escoge una variable de sustitucion "u" y se ponen las expresiones en términos de f(u) y u(x)=g(x) para el primer sumando.
     * Se escoge una variable de sustitucion "v" y se ponen las expresiones en términos de h(v) y v(x)=j(x) para el segundo sumando.
     * El método muestra los pasos de derivación con la regla de la cadena para cada sumando y efectúa la suma sibólica
     *
     * @param fu función de la variable de sustitución  (u)
     * @param gx función de la variable original (x)
     * @param hv función de la variable de sustitución  (u)
     * @param jx función de la variable original (x)
     * @return cadena con el proceso de derivación usando la regla de la cadena
     * @see class reglaCadena
     * @author Iván Gutiérrez
     * @deprecated derivaSimbolico puede sustituir a esta función.
     */
    public static String sumaReglaCadena(String fu, String gx, String hv, String jx) {
        String PLANTILLA_SYMPY = "from sympy import *\n" +
                "from sympy.parsing.latex import parse_latex\n" +
                "\n" +
                "\n" +
                "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                "init_printing()\n" +
                "x = var('x')\n" +
                "u = var('u')\n" +
                "v = var('v')\n" +
                "f = parse_latex(r\"$FU$\")\n" +
                "g = parse_latex(r\"$GX$\")\n" +
                "h = parse_latex(r\"$HV$\")\n" +
                "j = parse_latex(r\"$JX$\")\n" +
                "salida.write(\"$$\\\\displaystyle f(x)=%s$$<br/><br/>\\n\" % latex(f.subs(u,g)+h.subs(v,j)))\n" +
                "salida.write(\"$$\\\\displaystyle f(u)=%s$$<br/><br/>\\n\" % latex(f))\n" +
                "salida.write(\"$$\\\\displaystyle u(x)=%s$$<br/><br/>\\n\" % latex(g))\n" +
                "salida.write(\"$$\\\\displaystyle g(v)=%s$$<br/><br/>\\n\" % latex(h))\n" +
                "salida.write(\"$$\\\\displaystyle v(x)=%s$$<br/><br/>\\n\" % latex(j))\n" +
                "df = diff(f,u)\n" +
                "dg = diff(g,x)\n" +
                "dh = diff(h,v)\n" +
                "dj = diff(j,x)\n" +
//                "salida.write(\"$$\\\\displaystyle  \\\\frac{df}{du}=%s$$<br/><br/>\\n\" % latex(df))\n" +
//                "salida.write(\"$$ $$\\n\")\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}=%s$$<br/><br/>\\n\" % latex(df))\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{du}{dx}=%s$$<br/><br/>\\n\" % latex(dg))\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{dg}{dv}=%s$$<br/><br/>\\n\" % latex(dh))\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{dv}{dx}=%s$$<br/><br/>\\n\" % latex(dj))\n" +
                "result = df*dg+dh*dj\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{du}\\\\frac{du}{dx}+ \\\\frac{dg}{dv}\\\\frac{dv}{dx} = %s$$<br/><br/>\\n\" % latex(result))\n" +
                "result = result.subs(u,g)\n" +
                "result = result.subs(v,j)\n" +
                "salida.write(\"$$\\\\displaystyle \\\\frac{df}{dx}=%s=%s$$<br/><br/>\\n\" % (latex(result), latex(result.cancel())))\n" +
                "salida.close()";
        String script = PLANTILLA_SYMPY;

        script = script.replace("$FU$", fu);
        script = script.replace("$GX$", gx);
        script = script.replace("$HV$", hv);
        script = script.replace("$JX$", jx);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     *  Deriva simbólicamente de la suma de dos productos
     *  de la forma \f$y = uv + wz\f$.
     * @param u primer factor del primer sumando como función de x
     * @param v segundo factor del primer sumando como función de x
     * @param w primer factor del segundo sumando como función de x
     * @param z segundo factor del segundo sumando como función de x
     * @return String con la derivada.
     * @deprecated derivaSimbolico puede sustituir a esta función.
     * @author Iván Gutiérrez
     */
    public static String sumaProductosCadena(String u, String v, String w, String z){
        String PLANTILLA_SYMPY = "from sympy import *\n" +
                "from sympy.parsing.latex import parse_latex\n" +
                "#Solución a derivada de y=uv+wz\n" +
                "\n" +
                "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                "init_printing()\n" +
                "x = var('x')\n" +
                "u = var('u')\n" +
                "v = var('v')\n" +
                "w = var('w')\n" +
                "z = var('z')\n" +
                "u = parse_latex(r\"$U$\")\n" +
                "v = parse_latex(r\"$V$\")\n" +
                "w = parse_latex(r\"$W$\")\n" +
                "z = parse_latex(r\"$Z$\")\n" +
                "du = diff(u)\n" +
                "dv = diff(v)\n" +
                "dw = diff(w)\n" +
                "dz = diff(z)\n" +
                "salida.write(\"$$y=%s$$<br/><br/>\\n\" % latex(u*v+w*z))\n" +
                "salida.write(\"$$u=%s$$<br/><br/>\\n\" % latex(u))\n" +
                "salida.write(\"$$v=%s$$<br/><br/>\\n\" % latex(v))\n" +
                "salida.write(\"$$w=%s$$<br/><br/>\\n\" % latex(w))\n" +
                "salida.write(\"$$z=%s$$<br/><br/><br/>\\n\" % latex(z))\n" +
                "salida.write(\"$$u'=%s$$<br/><br/>\\n\" % latex(du))\n" +
                "salida.write(\"$$v'=%s$$<br/><br/>\\n\" % latex(dv))\n" +
                "salida.write(\"$$w'=%s$$<br/><br/>\\n\" % latex(dw))\n" +
                "salida.write(\"$$z'=%s$$<br/><br/><br/>\\n\" % latex(dz))\n" +
                "result = du*v+dv*u+dw*z+dz*w\n" +
                "salida.write(\"$$y'=(u\\'v+v\\'u)+(w\\'z+z\\'w)$$<br/><br/>\\n\")\n" +
                "salida.write(\"$$y'=[(%s) (%s) +(%s) (%s)]+[(%s) (%s)]+[(%s)(%s)]$$<br/><br/>\\n\"% ( latex(du), latex(v),latex(dv), latex(u), latex(dw), latex(z),latex(dz), latex(w) ) )\n" +
                "salida.write(\"$$y'=%s$$<br/>\\n\" % latex(result))\n" +
                "salida.close()"
                ;
        String script = PLANTILLA_SYMPY;
        script = script.replace("$U$", u);
        script = script.replace("$V$", v);
        script = script.replace("$W$", w);
        script = script.replace("$Z$", z);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     *  Deriva simbólicamente la suma de dos protencias
     *  de la forma \f$y = au^n+v^m\f$.
     * @param a coeficiente del primer sumando como función de x
     * @param ux segundo factor del primer sumando como función de x
     * @param n potencia del primer sumando
     * @param vx segundo sumando como función de x
     * @param m potencia del segundo sumando como función de x
     * @return String con la derivada.
     * @deprecated derivaSimbolico puede sustituir a esta función.
     * @author Iván Gutiérrez
     */

    public static String sumaPotenciasCadena(String a, String ux, String n, String vx, String m){
        String PLANTILLA_SYMPY = "from sympy import *\n" +
                "from sympy.parsing.latex import parse_latex\n" +
                "#Solución a derivada de y=uv+wz\n" +
                "\n" +
                "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                "init_printing()\n" +
                "x = var('x')\n" +
                "a = var('a')\n" +
                "u = var('u',commutative=false)\n" +
                "n = var('n')\n" +
                "v = var('v')\n" +
                "m = var('m')\n" +
                "a = UnevaluatedExpr(parse_latex(r\"$A$\"))\n" +
                "ux = parse_latex(r\"$UX$\",)\n" +
                "n = $N$\n" +
                "vx = parse_latex(r\"$VX$\")\n" +
                "m = $M$\n" +
                "f1 = u ** n\n" +
                "f2 = v ** m\n" +
                "fx = a*ux ** n + vx ** m\n" +
                "salida.write(\"$$f(x)=%s$$<br><br>\\n\" % latex(fx))\n" +
                "# u(x)\n" +
                "salida.write(\"$$u(x)=%s$$<br><br>\\n\" % latex(ux))\n" +
                "dfu = diff(a*u ** n,u).doit()\n" +
                "salida.write(\"$$\\\\frac{d}{du}(%s)=%s=%s$$<br><br>\\n\" % (latex(a*u ** n), latex(a*diff(u**n)), latex(dfu)))\n" +
                "dux = diff(ux)\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(ux), latex(dux)))\n" +
                "df1 = dfu * dux\n" +
                "salida.write(\"$$\\\\frac{d}{du}\\\\frac{du}{dx}=%s$$<br><br>\\n\" % latex(df1))\n" +
                "df1 = df1.subs(u, ux)\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(f1.subs(u, ux)), latex(df1)))\n" +
                "# v(x)\n" +
                "salida.write(\"$$v(x)=%s$$<br><br>\\n\" % latex(vx))\n" +
                "dfv = diff(v ** m)\n" +
                "salida.write(\"$$\\\\frac{d}{dv}(%s)=%s$$<br><br>\\n\" % (latex(v ** m), latex(dfv)))\n" +
                "dvx = diff(vx)\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(vx), latex(dvx)))\n" +
                "df2 = dfv * dvx\n" +
                "salida.write(\"$$\\\\frac{d}{du}\\\\frac{du}{dx}=%s$$<br><br>\\n\" % latex(df2))\n" +
                "df2 = df2.subs(v, vx)\n" +
                "\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(f2.subs(v, vx)), latex(df2)))\n" +
                "\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(fx), latex(df1.factor() + df2.factor())))\n"+
                "salida.close()"
                ;
        String script = PLANTILLA_SYMPY;
        script = script.replace("$UX$", ux);
        script = script.replace("$VX$", vx);
        script = script.replace("$N$", n);
        script = script.replace("$M$", m);
        script = script.replace("$A$", a);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     *  Deriva simbólicamente el producto de dos protencias
     *  de la forma \f$y = u^n*v^m\f$
     * @param ux primer factor como función de x
     * @param n potencia del primer factor
     * @param vx segundo factor como función de x
     * @param m potencia del segundo sumando como función de x
     * @return String con la derivada.
     * @deprecated derivaSimbolico puede sustituir a esta función.
     * @author Iván Gutiérrez
     */
    public static String productoPotenciasCadena(String ux, String n, String vx, String m){
        String PLANTILLA_SYMPY = "from sympy import *\n" +
                "from sympy.parsing.latex import parse_latex\n" +
                "#Solución a derivada de y=u^n*v^m\n" +
                "\n" +
                "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                "init_printing()\n" +
                "x = var('x')\n" +
                "u = var('u')\n" +
                "n = var('n')\n" +
                "v = var('v')\n" +
                "m = var('m')\n" +
                "ux = parse_latex(r\"$UX$\")\n" +
                "n = $N$\n" +
                "vx = parse_latex(r\"$VX$\")\n" +
                "m = $M$\n" +
                "f1 = u ** n\n" +
                "f2 = v ** m\n" +
                "fx = ux ** n * vx ** m\n" +
                "salida.write(\"$$f(x)=%s$$<br><br>\\n\" % latex(fx))\n" +
                "# u(x)\n" +
                "salida.write(\"$$u(x)=%s$$<br><br>\\n\" % latex(ux))\n" +
                "dfu = diff(u ** n)\n" +
                "salida.write(\"$$\\\\frac{d}{du}(%s)=%s$$<br><br>\\n\" % (latex(u ** n), latex(dfu)))\n" +
                "dux = diff(ux)\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(ux), latex(dux)))\n" +
                "df1 = dfu * dux\n" +
                "salida.write(\"$$\\\\frac{d}{du}\\\\frac{du}{dx}=%s$$<br><br>\\n\" % latex(df1))\n" +
                "df1 = df1.subs(u, ux)\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(f1.subs(u, ux)), latex(df1)))\n" +
                "# v(x)\n" +
                "salida.write(\"$$v(x)=%s$$<br><br>\\n\" % latex(vx))\n" +
                "dfv = diff(v ** m)\n" +
                "salida.write(\"$$\\\\frac{d}{dv}(%s)=%s$$<br><br>\\n\" % (latex(v ** m), latex(dfv)))\n" +
                "dvx = diff(vx)\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(vx), latex(dvx)))\n" +
                "df2 = dfv * dvx\n" +
                "salida.write(\"$$\\\\frac{d}{du}\\\\frac{du}{dx}=%s$$<br><br>\\n\" % latex(df2))\n" +
                "df2 = df2.subs(v, vx)\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=%s$$<br><br>\\n\" % (latex(f2.subs(v, vx)), latex(df2)))\n" +
                "\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(u^{n}(x) v^{m}(x))=v^{m}(x) nu^{n-1}(x)\\\\frac{du}{dx} + u^{n}(x)mv^{m-1}(x)\\\\frac{dv}{dx}$$<br><br>\\n\")\n" +
                "salida.write(\"$$\\\\frac{d}{dx}(%s)=[(%s)(%s)]+[(%s)(%s)]=%s$$<br><br>\\n\" % (latex(fx),latex(df1),latex(vx**m), latex(df2), latex(ux**n), latex(df1*vx**m + cancel(df2*ux**n))))\n"
                ;
        String script = PLANTILLA_SYMPY;
        script = script.replace("$UX$", ux);
        script = script.replace("$VX$", vx);
        script = script.replace("$N$", n);
        script = script.replace("$M$", m);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Deriva funciones de "x", regresa la derivación paso a paso.<p>
     * Construye un script Python con los scriptlets DERIVADOR,PARSER,SOLVER y CLOSER.<p>
     * Sustituye en el script la expresión en LaTeX pasada como parámetro.<p>
     * Ejecuta el script. <p>
     * Regresa la solución en HTML. <p>
     * @param expresion String en LaTeX a derivar
     * @param variableindep variable respecto de la que se derivará. Usualmente 'x'
     * @return String con solución paso a paso en HTML
     * @author Iván Gutiérrez
     */
    public static String derivaSimbolico(String expresion, String variableindep){
        String script = DERIVADOR+PARSER+SOLVER+CLOSER;
        script = script.replace("$EXPRESION$", expresion);
        script = script.replace("$VARIABLEIND$", variableindep);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Deriva funciones de "x", regresa la derivación paso a paso.<p>
     * Construye un script Python con los scriptlets DERIVADOR,PARSER,SOLVER y CLOSER.<p>
     * Sustituye en el script la expresión en LaTeX pasada como parámetro.<p>
     * Ejecuta el script. <p>
     * Regresa la solución en HTML. <p>
     * @param expresion String en LaTeX a derivar
     * @param variableindep
     * @return String con solución paso a paso en HTML
     * @author Iván Gutiérrez
     */
    public static String derivaSimbolicoSegunda(String expresion, String variableindep){
        String script = DERIVADOR+PARSER+SOLVER+DIFF_STEP+CLOSER;
        script = script.replace("$EXPRESION$", expresion);
        script = script.replace("$VARIABLEIND$", variableindep);
        String solucion = ejecutaPython(script);
        return solucion;
    }
    /**
     * Encuentra la recta tangente a funciones \f$f(x)\f$ en un punto \f$x_0\f$<p>
     * Regresa la derivación paso a paso y la ecuación de la recta.<p>
     * Construye un script Python con los scriptlets DERIVADOR,PARSER,SOLVER_RECTA_TANGENTE y CLOSER.<p>
     * Sustituye en el script la expresión en LaTeX pasada como parámetro.<p>
     * Ejecuta el script. <p>
     * Regresa la solución en HTML. <p>
     * @param expresion String en LaTeX a derivar
     * @param x0 abscisa del punto de tangencia
     * @param variableindep
     * @return String con solución paso a paso en HTML
     * @author Iván Gutiérrez
     */
    public static String rectaTangente(String expresion, Integer x0, String variableindep){
        String script = DERIVADOR+PARSER+SOLVER_RECTA_TANGENTE+CLOSER;
        script = script.replace("$EXPRESION$", expresion);
        script = script.replace("$VARIABLEIND$", variableindep);
        script = script.replace("$X0$", x0.toString());
        String solucion = ejecutaPython(script);
        return solucion;
    }
    /**
     * Encuentra la recta tangente a funciones \f$f(x)\f$ en un punto \f$x_0\f$<p>
     * Regresa la derivación paso a paso y la ecuación de la recta.<p>
     * Construye un script Python con los scriptlets DERIVADOR,PARSER,SOLVER_RECTA_TANGENTE y CLOSER.<p>
     * Sustituye en el script la expresión en LaTeX pasada como parámetro.<p>
     * Ejecuta el script. <p>
     * Regresa la solución en HTML. <p>
     * @param expresion String en LaTeX a derivar
     * @param x0 abscisa del punto de tangencia
     * @param variableindep variable independiente, uusalmente 'x'
     * @return String con solución paso a paso en HTML
     * @author Iván Gutiérrez
     */
    public static String rectaTangenteGrafica(String expresion, Integer x0, String variableindep){
        String script = DERIVADOR+PARSER+SOLVER_RECTA_TANGENTE_GRAFICA+CLOSER;
        script = script.replace("$EXPRESION$", expresion);
        script = script.replace("$VARIABLEIND$", variableindep);
        script = script.replace("$X0$", x0.toString());
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Encuentra las rectas tangentes horizontales \f$f(x)\f$<p>
     * Regresa la derivación paso a paso y la ecuación de la recta.<p>
     * Construye un script Python con los scriptlets DERIVADOR,PARSER,SOLVER_TANGENTE_HORIZONTAL y CLOSER.<p>
     * Sustituye en el script la expresión en LaTeX pasada como parámetro.<p>
     * Ejecuta el script. <p>
     * Regresa la solución en HTML. <p>
     * @param expresion String con función en LaTeX
     * @param variableindep
     * @return String en HTML con la solución paso a paso de la derivada
     * y las ecuaciones de las rectas horizontales
     * @author Iván Gutiérrez
     */
    public static String tangentesHorizontales(String expresion, String variableindep){
        String script = DERIVADOR+PARSER+SOLVER_TANGENTE_HORIZONTAL+CLOSER;
        script = script.replace("$EXPRESION$", expresion);
        script = script.replace("$VARIABLEIND$", variableindep);
        String solucion = ejecutaPython(script);
        return solucion;
    }
    /**
     * Encuentra las rectas tangentes horizontales \f$f(x)\f$<p>
     * Regresa la derivación paso a paso y la ecuación de la recta.<p>
     * Construye un script Python con los scriptlets DERIVADOR,PARSER,SOLVER_TANGENTE_HORIZONTAL_GRAFICA_SVG y CLOSER.<p>
     * Sustituye en el script la expresión en LaTeX pasada como parámetro.<p>
     * Ejecuta el script. <p>
     * Regresa la solución en HTML. <p>
     * @param expresion String con función en LaTeX
     * @param variableindep
     * @return String en HTML con la solución paso a paso de la derivada
     * y las ecuaciones de las rectas horizontales
     * @author Iván Gutiérrez
     */
    public static String tangentesHorizontalesGraficaSVG(String expresion, String variableindep){
        String script = DERIVADOR+PARSER+ SOLVER_TANGENTE_HORIZONTAL_GRAFICA_JSX +CLOSER;
        script = script.replace("$EXPRESION$", expresion);
        script = script.replace("$VARIABLEIND$", variableindep);
        String solucion = ejecutaPython(script);
        return solucion;
    }
    /**
     * Encuentra las rectas tangentes horizontales \f$f(x)\f$<p>
     * Regresa la derivación paso a paso y la ecuación de la recta.<p>
     * Construye un script Python con los scriptlets DERIVADOR,PARSER,SOLVER_TANGENTE_HORIZONTAL_GRAFICA_JSX y CLOSER.<p>
     * Sustituye en el script la expresión en LaTeX pasada como parámetro.<p>
     * Ejecuta el script. <p>
     * Regresa la solución en HTML. <p>
     * @param expresion String con función en LaTeX
     * @param variableindep
     * @return String en HTML con la solución paso a paso de la derivada
     * y las ecuaciones de las rectas horizontales
     * @author Iván Gutiérrez
     */
    public static String tangentesHorizontalesGraficaJSX(String expresion, String variableindep){
        String script = DERIVADOR+PARSER+ SOLVER_TANGENTE_HORIZONTAL_GRAFICA_JSX +CLOSER;
        script = script.replace("$EXPRESION$", expresion);
        script = script.replace("$VARIABLEIND$", variableindep);
        String solucion = ejecutaPython(script);
        return solucion;
    }

    /**
     * Simplifica algebráicamente la expresión pasada como parámetro.
     * @param expresion  String expresión algebráica en LaTeX a simplificar.
     * @return String expresion simplificada en LaTeX.
     * @author Iván Gutiérrez
     */
    public static String simplifica(String expresion){
        String PLANTILLA_SYMPY =
                "from sympy import *\n" +
                        "from sympy.parsing.latex import parse_latex\n" +
                        "salida = open(\"/tmp/solucion_$UUID$.txt\",\"w\")\n" +
                        "init_printing()\n" +
                        "x = var('x')\n" +
                        "f = parse_latex(r\" $EXPRESION$ \")\n" +
                        "f = expand(f) \n" +
//                        "f = collect(f,x) \n" +
                        "salida.write(\"%s\" %(latex(f)))\n" ;
        String script = PLANTILLA_SYMPY;
        script = script.replace("$EXPRESION$", expresion);
        String solucion = ejecutaPython(script);
        return solucion;
    }

}
