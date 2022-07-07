"""
   Package lothon.process.compute
   Module  compute_matricial.py

"""

__all__ = [
    'ComputeMatricial'
]

# ----------------------------------------------------------------------------
# DEPENDENCIAS
# ----------------------------------------------------------------------------

# Built-in/Generic modules
from typing import Optional
import itertools as itt
import logging

# Libs/Frameworks modules
# Own/Project modules
from lothon.util.eve import *
from lothon.stats import combinatoria as cb
from lothon.domain import Loteria, Concurso
from lothon.process.compute.abstract_compute import AbstractCompute


# ----------------------------------------------------------------------------
# VARIAVEIS GLOBAIS
# ----------------------------------------------------------------------------

# obtem uma instancia do logger para o modulo corrente:
logger = logging.getLogger(__name__)


# ----------------------------------------------------------------------------
# CLASSE CONCRETA
# ----------------------------------------------------------------------------

class ComputeMatricial(AbstractCompute):
    """
    Implementacao de classe para .
    """

    # --- PROPRIEDADES -------------------------------------------------------
    __slots__ = ('colunas_jogos', 'colunas_percentos', 'colunas_concursos',
                 'ultimas_colunas_repetidas', 'ultimas_colunas_percentos',
                 'max_colunas_ultimo_concurso', 'max_colunas_penultimo_concurso',
                 'linhas_jogos', 'linhas_percentos', 'linhas_concursos', 
                 'ultimas_linhas_repetidas', 'ultimas_linhas_percentos',
                 'max_linhas_ultimo_concurso', 'max_linhas_penultimo_concurso',
                 'qtd_zerados', 'penultimas_colunas_repetidas', 'penultimas_linhas_repetidas')

    # --- INICIALIZACAO ------------------------------------------------------

    def __init__(self):
        super().__init__("Computacao Matricial dos Concursos")

        # estruturas para a coleta de dados a partir do processamento de analise:
        self.colunas_jogos: Optional[list[int]] = None
        self.colunas_percentos: Optional[list[float]] = None
        self.colunas_concursos: Optional[list[int]] = None
        self.ultimas_colunas_repetidas: Optional[list[int]] = None
        self.penultimas_colunas_repetidas: Optional[list[int]] = None
        self.ultimas_colunas_percentos: Optional[list[float]] = None
        self.max_colunas_ultimo_concurso: int = 0
        self.max_colunas_penultimo_concurso: int = 0
        self.linhas_jogos: Optional[list[int]] = None
        self.linhas_percentos: Optional[list[float]] = None
        self.linhas_concursos: Optional[list[int]] = None
        self.ultimas_linhas_repetidas: Optional[list[int]] = None
        self.penultimas_linhas_repetidas: Optional[list[int]] = None
        self.ultimas_linhas_percentos: Optional[list[float]] = None
        self.max_linhas_ultimo_concurso: int = 0
        self.max_linhas_penultimo_concurso: int = 0
        self.qtd_zerados: int = 0

    def setup(self, parms: dict):
        # absorve os parametros fornecidos:
        super().setup(parms)

    # --- PROCESSAMENTO ------------------------------------------------------

    def execute(self, loteria: Loteria) -> int:
        # valida se possui concursos a serem analisados:
        if loteria is None or loteria.concursos is None or len(loteria.concursos) == 0:
            return -1
        else:
            _startWatch = startwatch()

        # identifica informacoes da loteria:
        nmlot: str = loteria.nome_loteria
        qtd_jogos: int = loteria.qtd_jogos
        concursos: list[Concurso] = loteria.concursos
        qtd_concursos: int = len(concursos)
        qtd_items: int = loteria.qtd_bolas_sorteio

        # efetua analise de todas as combinacoes de jogos da loteria:
        self.colunas_jogos = cb.new_list_int(qtd_items)
        self.linhas_jogos = cb.new_list_int(qtd_items)

        # identifica o numero maximo de colunas e linhas de cada combinacao de jogo:
        range_jogos: range = range(1, loteria.qtd_bolas + 1)
        for jogo in itt.combinations(range_jogos, loteria.qtd_bolas_sorteio):
            # maximo de colunas
            vl_max_col: int = cb.max_colunas(jogo)
            self.colunas_jogos[vl_max_col] += 1

            # maximo de linhas
            vl_max_lin: int = cb.max_linhas(jogo)
            self.linhas_jogos[vl_max_lin] += 1

        # contabiliza o percentual das colunas:
        self.colunas_percentos = cb.new_list_float(qtd_items)
        for key, value in enumerate(self.colunas_jogos):
            percent: float = round((value / qtd_jogos) * 10000) / 100
            self.colunas_percentos[key] = percent

        # contabiliza o percentual das linhas:
        self.linhas_percentos = cb.new_list_float(qtd_items)
        for key, value in enumerate(self.linhas_jogos):
            percent: float = round((value / qtd_jogos) * 10000) / 100
            self.linhas_percentos[key] = percent

        # zera os contadores de cada sequencia:
        self.colunas_concursos = cb.new_list_int(qtd_items)
        self.linhas_concursos = cb.new_list_int(qtd_items)
        self.ultimas_colunas_repetidas = cb.new_list_int(qtd_items)
        self.ultimas_linhas_repetidas = cb.new_list_int(qtd_items)

        self.penultimas_colunas_repetidas = cb.new_list_int(qtd_items)
        self.penultimas_linhas_repetidas = cb.new_list_int(qtd_items)

        # identifica o numero maximo de colunas e linhas de cada sorteio ja realizado:
        self.max_colunas_ultimo_concurso = -1
        self.max_colunas_penultimo_concurso = -1
        self.max_linhas_ultimo_concurso = -1
        self.max_linhas_penultimo_concurso = -1
        for concurso in concursos:
            # maximo de colunas
            vl_max_col: int = cb.max_colunas(concurso.bolas)
            self.colunas_concursos[vl_max_col] += 1
            # verifica se repetiu o numero maximo de colunas do ultimo concurso:
            if vl_max_col == self.max_colunas_ultimo_concurso:
                self.ultimas_colunas_repetidas[vl_max_col] += 1
            if vl_max_col == self.max_colunas_ultimo_concurso == self.max_colunas_penultimo_concurso:
                self.penultimas_colunas_repetidas[vl_max_col] += 1
            # atualiza ambos flags, para ultimo e penultimo concursos
            self.max_colunas_penultimo_concurso = self.max_colunas_ultimo_concurso
            self.max_colunas_ultimo_concurso = vl_max_col

            # maximo de linhas
            vl_max_lin: int = cb.max_linhas(concurso.bolas)
            self.linhas_concursos[vl_max_lin] += 1
            # verifica se repetiu o numero maximo de linhas do ultimo concurso:
            if vl_max_lin == self.max_linhas_ultimo_concurso:
                self.ultimas_linhas_repetidas[vl_max_lin] += 1
            if vl_max_lin == self.max_linhas_ultimo_concurso == self.max_linhas_penultimo_concurso:
                self.penultimas_linhas_repetidas[vl_max_col] += 1
            # atualiza ambos flags, para ultimo e penultimo concursos
            self.max_linhas_penultimo_concurso = self.max_linhas_ultimo_concurso
            self.max_linhas_ultimo_concurso = vl_max_lin

        # contabiliza o percentual das ultimas maximas colunas:
        self.ultimas_colunas_percentos = cb.new_list_float(qtd_items)
        for key, value in enumerate(self.ultimas_colunas_repetidas):
            percent: float = round((value / qtd_concursos) * 10000) / 100
            self.ultimas_colunas_percentos[key] = percent

        # contabiliza o percentual das ultimas maximas linhas:
        self.ultimas_linhas_percentos = cb.new_list_float(qtd_items)
        for key, value in enumerate(self.ultimas_linhas_repetidas):
            percent: float = round((value / qtd_concursos) * 10000) / 100
            self.ultimas_linhas_percentos[key] = percent

        print("***** self.penultimas_colunas_repetidas = ", self.penultimas_colunas_repetidas)
        print("***** self.penultimas_linhas_repetidas = ", self.penultimas_linhas_repetidas)

        _stopWatch = stopwatch(_startWatch)
        logger.info(f"{nmlot}: Tempo para executar {self.id_process.upper()}: {_stopWatch}")
        return 0

    # --- ANALISE E AVALIACAO DE JOGOS ---------------------------------------

    def evaluate(self, ordinal: int, jogo: tuple) -> float:
        # probabilidade de acerto depende do numero maximo de colunas e linhas do jogo:
        vl_max_col: int = cb.max_colunas(jogo)
        percent_col: float = self.colunas_percentos[vl_max_col]

        vl_max_lin: int = cb.max_linhas(jogo)
        percent_lin: float = self.linhas_percentos[vl_max_lin]

        # ignora valores muito baixos de probabilidade:
        if percent_col < 9 or percent_lin < 5:
            self.qtd_zerados += 1
            return 0

        # calcula o fator de linhas e colunas juntas, para facilitar o resto da funcao:
        fator_percent: float = to_fator(percent_col) * to_fator(percent_lin)

        # verifica se esse jogo repetiu a maxima coluna e/ou linha do ultimo e penultimo concursos:
        if vl_max_col != self.max_colunas_ultimo_concurso and \
                vl_max_lin != self.max_linhas_ultimo_concurso:
            return fator_percent  # nao repetiu, ja pode pular fora

        elif vl_max_col == self.max_colunas_ultimo_concurso == self.max_colunas_penultimo_concurso \
                or vl_max_lin == self.max_linhas_ultimo_concurso == \
                self.max_linhas_penultimo_concurso:
            self.qtd_zerados += 1
            return 0  # pouco provavel de repetir mais de 2 ou 3 vezes

        # se repetiu a maxima de colunas, obtem a probabilidade de repeticao da ultima maxima:
        if vl_max_col == self.max_colunas_ultimo_concurso:
            percent_col_repetida: float = self.ultimas_colunas_percentos[vl_max_col]
            if percent_col_repetida < 1:  # baixa probabilidade pode ser descartada
                self.qtd_zerados += 1
                return 0
            else:  # reduz a probabilidade porque esse jogo vai repetir a coluna:
                fator_percent *= to_redutor(percent_col_repetida)

        # se repetiu a maxima de linhas, obtem a probabilidade de repeticao da ultima maxima:
        if vl_max_lin == self.max_linhas_ultimo_concurso:
            percent_lin_repetida: float = self.ultimas_linhas_percentos[vl_max_lin]
            if percent_lin_repetida < 1:  # baixa probabilidade pode ser descartada
                self.qtd_zerados += 1
                return 0
            else:  # reduz a probabilidade porque esse jogo vai repetir a coluna:
                fator_percent *= to_redutor(percent_lin_repetida)

        return fator_percent

# ----------------------------------------------------------------------------
