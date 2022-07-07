"""
   Package lothon.process.compute
   Module  bet_dia_de_sorte.py

"""

__all__ = [
    'BetDiaDeSorte'
]

# ----------------------------------------------------------------------------
# DEPENDENCIAS
# ----------------------------------------------------------------------------

# Built-in/Generic modules
from typing import Optional, Any
import itertools as itt
import logging

# Libs/Frameworks modules
# Own/Project modules
from lothon.util.eve import *
# from lothon.stats import combinatoria as cb
from lothon.domain import Loteria, Concurso, Jogo
from lothon.process.betting.abstract_betting import AbstractBetting
from lothon.process.compute.abstract_compute import AbstractCompute
from lothon.process.compute.compute_ausencia import ComputeAusencia
from lothon.process.compute.compute_espacamento import ComputeEspacamento
from lothon.process.compute.compute_frequencia import ComputeFrequencia
from lothon.process.compute.compute_matricial import ComputeMatricial
from lothon.process.compute.compute_mediana import ComputeMediana
from lothon.process.compute.compute_paridade import ComputeParidade
from lothon.process.compute.compute_recorrencia import ComputeRecorrencia
from lothon.process.compute.compute_repetencia import ComputeRepetencia
from lothon.process.compute.compute_sequencia import ComputeSequencia


# ----------------------------------------------------------------------------
# VARIAVEIS GLOBAIS
# ----------------------------------------------------------------------------

# obtem uma instancia do logger para o modulo corrente:
logger = logging.getLogger(__name__)


# ----------------------------------------------------------------------------
# FUNCOES HELPERS
# ----------------------------------------------------------------------------

# apenas as computacoes com valores mais significativos, apos analises e simulados:
def get_process_chain() -> list[AbstractCompute]:
    return [
        ComputeParidade(),
        ComputeSequencia(),
        ComputeEspacamento(),
        ComputeMediana(),
        ComputeMatricial(),
        ComputeAusencia(),
        ComputeFrequencia(),
        ComputeRepetencia(),
        ComputeRecorrencia()
    ]


# ----------------------------------------------------------------------------
# CLASSE CONCRETA
# ----------------------------------------------------------------------------

class BetDiaDeSorte(AbstractBetting):
    """
    Implementacao de classe para .
    """

    # --- PROPRIEDADES -------------------------------------------------------
    __slots__ = ()

    # --- INICIALIZACAO ------------------------------------------------------

    def __init__(self, loteria: Loteria):
        super().__init__("Geracao de Jogos para 'Dia de Sorte'", loteria)

        # estruturas para a coleta de dados a partir do processamento de analise:

    def setup(self, parms: dict):
        # absorve os parametros fornecidos:
        super().setup(parms)

    # --- METODOS ------------------------------------------------------------

    @classmethod
    def get_jogo_concurso(cls, bolas: tuple[int, ...], jogos: list[Jogo]) -> Optional[Jogo]:
        # procura na lista de jogos para identificar o jogo correspondente ao concurso (bolas):
        for jogo in jogos:
            if bolas == jogo.dezenas:
                return jogo

        # se percorreu toda a lista de jogos e nao encontrou, retorna vazio:
        return None

    @classmethod
    def get_ordinal_concurso(cls, bolas: tuple[int, ...], jogos: list[Jogo]) -> int:
        # procura na lista de jogos para identificar o ordinal do jogo correspondente:
        for idx, jogo in enumerate(jogos):
            if bolas == jogo.dezenas:
                return idx

        # se percorreu toda a lista de jogos e nao encontrou, entao informa que ha algo errado:
        return -1

    # --- PROCESSAMENTO ------------------------------------------------------

    def execute(self, concursos: list[Concurso] = None) -> Optional[list[tuple[int, ...]]]:
        # valida se possui concursos a serem analisados:
        if concursos is not None:
            if len(concursos) > 0:
                self.concursos = concursos
            else:
                return None
        _startWatch = startwatch()

        # identifica informacoes da loteria:
        concursos_passados: list[Concurso] = self.concursos[:-1]
        ultimo_concurso: Concurso = self.concursos[-1]
        qtd_bolas: int = self.loteria.qtd_bolas
        qtd_bolas_sorteio: int = self.loteria.qtd_bolas_sorteio
        qtd_jogos: int = self.loteria.qtd_jogos

        # inicializa a cadeia de processos para computacao de jogos:
        compute_chain: list[AbstractCompute] = get_process_chain()

        # define os parametros para configurar o processamento de 'evaluate()' dos processos:
        parms: dict[str: Any] = {  # aplica limites e/ou faixas de corte...
            'qtd_bolas': qtd_bolas,
            'qtd_bolas_sorteio': qtd_bolas_sorteio,
            'qtd_jogos': qtd_jogos,
            'min_threshold': 10  # define o percentual de corte, ignorando rates abaixo de 10%
        }
        # configura cada um dos processos de calculo-evaluate, para computarem os sorteios:
        logger.debug("Configurando a cadeia de processos para computacao de jogos.")
        for cproc in compute_chain:
            # configuracao de parametros para os processamentos em cada classe de analise:
            logger.debug(f"Processo '{cproc.id_process}': configurando parametros de SETUP...")
            cproc.setup(parms)

        # Efetua a execucao de cada processo de analise em sequencia (chain) para coleta de dados:
        logger.debug("Executando o processamento das loterias para computacao de jogos.")
        for cproc in compute_chain:
            # executa a analise para cada loteria:
            logger.debug(f"Processo '{cproc.id_process}': executando computacao dos sorteios...")
            cproc.execute(concursos_passados)

        # efetua analise geral (evaluate) de todas as combinacoes de jogos da loteria:
        jogos_computados: list[Jogo] = []
        qtd_zerados: int = 0

        # gera as combinacoes de jogos da loteria:
        range_jogos: range = range(1, qtd_bolas + 1)
        vl_ordinal: int = 0
        for jogo in itt.combinations(range_jogos, qtd_bolas_sorteio):
            vl_ordinal += 1  # primeiro jogo ira comecar do #1

            # executa a avaliacao do jogo, para verificar se sera considerado ou descartado:
            vl_fator: float = 0
            for cproc in compute_chain:
                vl_eval: float = cproc.eval(vl_ordinal, jogo)

                # ignora o resto das analises se a metrica zerou:
                if vl_eval > 0:
                    vl_fator += vl_eval  # probabilidade da uniao de dois eventos
                else:
                    vl_fator = 0  # zera o fator para que o jogo nao seja considerado
                    break  # ignora e pula para o proximo jogo, acelerando o processamento

            # se a metrica atingir o ponto de corte, entao mantem o jogo para apostar:
            if vl_fator > 0:
                jogos_computados.append(Jogo(vl_ordinal, vl_fator, jogo))
            else:
                qtd_zerados += 1

        logger.info(f"Finalizado o processamento das  {formatd(qtd_jogos)}  combinacoes de jogos. "
                    f" Eliminados (zerados)  {formatd(qtd_zerados)}  jogos.")

        # ordena os jogos processados pelo fator, do maior (maiores chances) para o menor:
        jogos_computados.sort(key=lambda n: n.fator, reverse=True)

        # procura na lista de jogos computados o jogo correspondente ao ultimo sorteio:
        jogo_concurso: Jogo = self.get_jogo_concurso(ultimo_concurso.bolas, jogos_computados)
        ordinal_concurso: int = self.get_ordinal_concurso(ultimo_concurso.bolas, jogos_computados)

        logger.debug(f"Para o ultimo concurso #{formatd(ultimo_concurso.id_concurso)} foi "
                     f"encontrado o jogo[{formatd(ordinal_concurso)}]:\n\t {jogo_concurso}")

        output: str = f"\n\t   ORDINAL     FATOR     DEZENAS\n"
        for idx, jogo in enumerate(jogos_computados):
            output += f"\t{formatd(idx,10)}    {formatf(jogo.fator,'6.3')}     {jogo.dezenas}\n"
        logger.debug(f"Finalizou a impressao dos  {formatd(len(jogos_computados))}  jogos "
                     f"computados e considerados: {output}")

        _stopWatch = stopwatch(_startWatch)
        logger.info(f"Tempo para executar {self.id_process.upper()}: {_stopWatch}")
        return None

# ----------------------------------------------------------------------------
